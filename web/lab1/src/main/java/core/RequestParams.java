package core;

import java.util.Map;

public record RequestParams(int x, double y, int r, String sid) {

  public static RequestParams parse(Map<String, String> q) {
    int x = parseIntInSet(q.get("x"), -4, -3, -2, -1, 0, 1, 2, 3, 4);
    double y = parseDoubleInRange(q.get("y"), -5.0, 3.0);
    int r = parseIntInSet(q.get("r"), 1, 2, 3, 4, 5);
    String sid = (q.getOrDefault("sid", "").isBlank() ? "anonymous" : q.get("sid").trim());
    return new RequestParams(x, y, r, sid);
  }

  private static int parseIntInSet(String s, int... allowed) {
    int v = Integer.parseInt(nonEmpty(s));
    for (int a : allowed) if (a == v) return v;
    throw new IllegalArgumentException("value not in allowed set");
  }

  private static double parseDoubleInRange(String s, double lo, double hi) {
    double v = Double.parseDouble(nonEmpty(s));
    if (Double.isFinite(v) && v >= lo && v <= hi) return v;
    throw new IllegalArgumentException("value out of range");
  }

  private static String nonEmpty(String s) {
    if (s == null || s.isBlank()) throw new IllegalArgumentException("missing param");
    return s.trim();
  }
}
