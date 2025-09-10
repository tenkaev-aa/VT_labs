import com.fastcgi.FCGIInterface;
import core.ApiResponse;
import core.HistoryStore;
import core.HitTester;
import core.RequestParams;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import util.Json;

public final class ServerMain {

  private static final HistoryStore HISTORY = new HistoryStore(1000);

  public static void main(String[] args) {

    FCGIInterface fcgi = new FCGIInterface();

    // main
    while (fcgi.FCGIaccept() >= 0) {
      final long t0 = System.nanoTime();
      try {
        Properties env = System.getProperties();
        String method = env.getProperty("REQUEST_METHOD", "GET");
        String qs = env.getProperty("QUERY_STRING", "");

        if (!"GET".equalsIgnoreCase(method)) {
          write(405, Json.error(405, "Only GET is allowed"));
          flush();
          continue;
        }

        Map<String, String> q = parseQuery(qs);
        String sid = q.getOrDefault("sid", "anonymous").trim();
        if (sid.isEmpty()) sid = "anonymous";

        try {
          // парсинг и валидация
          RequestParams p = RequestParams.parse(q);

          boolean hit = HitTester.isHit(p.x(), p.y(), p.r());
          double execMs = (System.nanoTime() - t0) / 1_000_000.0;

          // история
          HISTORY.add(p.x(), p.y(), p.r(), hit, sid);

          ApiResponse.Data data = ApiResponse.makeData(p.x(), p.y(), p.r(), hit, execMs);
          List<HistoryStore.Entry> history =
              HISTORY.latestBySid(sid, 50); //  latestBySid(sid, 50) latest(50)

          write(200, Json.ok(data, history));
        } catch (IllegalArgumentException ex) {
          write(400, Json.error(400, ex.getMessage()));
        }
      } catch (Throwable t) {
        t.printStackTrace();
        safeWrite(500, Json.error(500, "Internal Server Error"));
      } finally {
        flush();
      }
    }
  }

  private static Map<String, String> parseQuery(String qs) {
    Map<String, String> m = new LinkedHashMap<>();
    if (qs == null || qs.isEmpty()) return m;
    for (String p : qs.split("&")) {
      int k = p.indexOf('=');
      if (k >= 0) {
        m.put(urlDecode(p.substring(0, k)), urlDecode(p.substring(k + 1)));
      } else {
        m.put(urlDecode(p), "");
      }
    }
    return m;
  }

  private static String urlDecode(String s) {
    try {
      return java.net.URLDecoder.decode(s, StandardCharsets.UTF_8);
    } catch (Exception e) {
      return s;
    }
  }

  // HTTP заголовки
  private static void write(int status, String body) {
    final String reason =
        switch (status) {
          case 200 -> "OK";
          case 400 -> "Bad Request";
          case 405 -> "Method Not Allowed";
          default -> "Internal Server Error";
        };
    byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

    PrintStream out = System.out;
    out.printf("HTTP/1.1 %d %s\r\n", status, reason);
    out.print("Content-Type: application/json; charset=utf-8\r\n");
    out.printf("Content-Length: %d\r\n", bytes.length);
    out.print("Cache-Control: no-store\r\n");
    out.print("X-Powered-By: Java-FastCGI\r\n");
    out.print("\r\n"); // конец заголовков
    out.write(bytes, 0, bytes.length);
  }

  private static void safeWrite(int status, String body) {
    try {
      write(status, body);
    } catch (Exception ignored) {
    }
  }

  private static void flush() {
    try {
      System.out.flush();
    } catch (Exception ignore) {
    }
    try {
      System.err.flush();
    } catch (Exception ignore) {
    }
  }
}
