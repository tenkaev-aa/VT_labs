package core;

import java.time.OffsetDateTime;
import java.util.List;

public record ApiResponse(boolean ok, Object data, String error, List<HistoryStore.Entry> history) {
  public static record Data(
      int x, double y, int r, boolean hit, String serverTime, double execTimeMs) {}

  public static Data makeData(int x, double y, int r, boolean hit, double execMs) {
    return new Data(x, y, r, hit, OffsetDateTime.now().toString(), execMs);
  }
}
