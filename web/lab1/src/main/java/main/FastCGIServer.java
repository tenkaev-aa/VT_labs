package main;

import com.fastcgi.FCGIInterface;
import core.ApiResponse;
import java.util.Objects;
import util.JsonUtil;

public final class FastCGIServer {
  private final FCGIInterface fcgi;
  private final PointService pointService;
  private final ResponseWriter writer;
  private volatile boolean running = true;

  public FastCGIServer(FCGIInterface fcgi, PointService svc, ResponseWriter writer) {
    this.fcgi = Objects.requireNonNull(fcgi);
    this.pointService = Objects.requireNonNull(svc);
    this.writer = Objects.requireNonNull(writer);
  }

  public void run() {
    while (running && fcgi.FCGIaccept() >= 0) {
      long t0 = System.nanoTime();
      try {
        handleOnce(t0);
      } catch (Throwable t) {
        t.printStackTrace();
        writer.safeWriteJson(500, JsonUtil.error(500, "Internal Server Error"));
      } finally {
        writer.flushBoth();
      }
    }
  }

  public void stop() {
    running = false;
  }

  private void handleOnce(long t0) {
    RequestContext rc = RequestContext.from(System.getProperties());
    if (!rc.isGet()) {
      writer.writeJson(405, JsonUtil.error(405, "Only GET is allowed"));
      return;
    }

    ApiResponse resp = pointService.process(rc, t0);

    writer.writeJson(200, JsonUtil.ok((ApiResponse.Data) resp.data(), resp.history()));
  }
}
