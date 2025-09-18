package main;

import core.ApiResponse;
import core.HistoryStore;
import core.HitTester;
import core.RequestParams;

final class PointService {
  private final HistoryStore history;

  PointService(HistoryStore history) { this.history = history; }

  ApiResponse process(RequestContext rc, long t0) {
    RequestParams p = RequestParams.parse(rc.query);
    boolean hit = HitTester.isHit(p.x(), p.y(), p.r());
    double execMs = (System.nanoTime() - t0) / 1_000_000.0;

    history.add(p.x(), p.y(), p.r(), hit, rc.sid);
    var data = ApiResponse.makeData(p.x(), p.y(), p.r(), hit, execMs);
    var list = history.latestBySid(rc.sid, 50);
    return new ApiResponse(true, data, null, list);
  }
}