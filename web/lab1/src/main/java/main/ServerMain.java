package main;

import com.fastcgi.FCGIInterface;
import core.HistoryStore;

public final class ServerMain {
  public static void main(String[] args) {
    HistoryStore history = new HistoryStore(1000);

    FCGIInterface fcgi = new FCGIInterface();
    ResponseWriter writer = new ResponseWriter();
    PointService pointService = new PointService(history);

    FastCGIServer server = new FastCGIServer(fcgi, pointService, writer);

    server.run();
  }
}
