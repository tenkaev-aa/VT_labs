package main;

final class ResponseWriter {
  void writeJson(int status, String body) {
    String reason = switch (status) {
      case 200 -> "OK";
      case 400 -> "Bad Request";
      case 405 -> "Method Not Allowed";
      default -> "Internal Server Error";
    };
    byte[] bytes = body.getBytes(java.nio.charset.StandardCharsets.UTF_8);

    var out = System.out;
    out.printf("HTTP/1.1 %d %s\r\n", status, reason);
    out.print("Content-Type: application/json; charset=utf-8\r\n");
    out.printf("Content-Length: %d\r\n", bytes.length);
    out.print("Cache-Control: no-store\r\n");
    out.print("X-Powered-By: Java-FastCGI\r\n");
    out.print("\r\n");
    out.write(bytes, 0, bytes.length);
  }

  void safeWriteJson(int status, String body) {
    try { writeJson(status, body); } catch (Exception ignore) {}
  }

  void flushBoth() {
    try { System.out.flush(); } catch (Exception ignore) {}
    try { System.err.flush(); } catch (Exception ignore) {}
  }
}