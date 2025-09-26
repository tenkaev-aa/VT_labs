package core;

import java.time.OffsetDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public final class HistoryStore {

  public record Entry(int x, double y, int r, boolean hit, String ts, String sid) {}

  private final Deque<Entry> dq = new ArrayDeque<>();
  private final int limit;

  public HistoryStore(int limit) {
    this.limit = Math.max(1, limit);
  }

  public synchronized void add(int x, double y, int r, boolean hit, String sid) {
    dq.addFirst(new Entry(x, y, r, hit, OffsetDateTime.now().toString(), sid));
    while (dq.size() > limit) dq.removeLast();
  }

  public synchronized List<Entry> latest(int n) {
    int need = Math.max(0, n);
    ArrayList<Entry> out = new ArrayList<>(Math.min(need, dq.size()));
    int i = 0;
    for (Entry e : dq) {
      if (i++ >= need) break;
      out.add(e);
    }
    return out;
  }

  public synchronized List<Entry> latestBySid(String sid, int n) {
    int need = Math.max(0, n);
    ArrayList<Entry> out = new ArrayList<>();
    for (Entry e : dq) {
      if (sid.equals(e.sid)) out.add(e);
      if (out.size() >= need) break;
    }
    return out;
  }
}
