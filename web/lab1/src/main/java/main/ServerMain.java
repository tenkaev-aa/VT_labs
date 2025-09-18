package main;

import com.fastcgi.FCGIInterface;
import core.ApiResponse;
import core.HistoryStore;
import util.JsonUtil;

public final class ServerMain {
  private static final HistoryStore HISTORY = new HistoryStore(1000);

  public static void main(String[] args) {
    FCGIInterface fcgi = new FCGIInterface();
    ResponseWriter writer = new ResponseWriter();

    while (fcgi.FCGIaccept() >= 0) {
      long t0 = System.nanoTime();
      try {
        RequestContext rc = RequestContext.from(System.getProperties());
        if (!rc.isGet()) {
          writer.writeJson(405, JsonUtil.error(405, "Only GET is allowed"));
          continue;
        }

        PointService svc = new PointService(HISTORY);
        ApiResponse result = svc.process(rc, t0); // валидирует, считает, пишет в историю

        writer.writeJson(200, JsonUtil.ok((ApiResponse.Data) result.data(), result.history()));

      } catch (IllegalArgumentException ex) {
        writer.writeJson(400, JsonUtil.error(400, ex.getMessage()));
      } catch (Throwable t) {
        t.printStackTrace();
        writer.safeWriteJson(500, JsonUtil.error(500, "Internal Server Error"));
      } finally {
        writer.flushBoth();
      }
    }
  }
}