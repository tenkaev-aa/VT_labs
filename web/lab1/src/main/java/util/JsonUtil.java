package util;

import core.ApiResponse;
import core.HistoryStore;
import java.util.List;

public final class JsonUtil {
  private JsonUtil() {}

  public static String esc(String s) {
    if (s == null) return "null";
    StringBuilder sb = new StringBuilder(s.length() + 8);
    sb.append('"');
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      switch (c) {
        case '"' -> sb.append("\\\"");
        case '\\' -> sb.append("\\\\");
        case '\b' -> sb.append("\\b");
        case '\f' -> sb.append("\\f");
        case '\n' -> sb.append("\\n");
        case '\r' -> sb.append("\\r");
        case '\t' -> sb.append("\\t");
        default -> {
          if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
          else sb.append(c);
        }
      }
    }
    sb.append('"');
    return sb.toString();
  }

  public static String ok(ApiResponse.Data data, List<HistoryStore.Entry> history) {
    StringBuilder sb = new StringBuilder(1024);
    sb.append("{\"ok\":true,\"data\":{")
        .append("\"x\":")
        .append(data.x())
        .append(',')
        .append("\"y\":")
        .append(fmt(data.y()))
        .append(',')
        .append("\"r\":")
        .append(data.r())
        .append(',')
        .append("\"hit\":")
        .append(data.hit())
        .append(',')
        .append("\"serverTime\":")
        .append(esc(data.serverTime()))
        .append(',')
        .append("\"execTimeMs\":")
        .append(fmt(data.execTimeMs()))
        .append("},\"history\":[");
    for (int i = 0; i < history.size(); i++) {
      if (i > 0) sb.append(',');
      var h = history.get(i);
      sb.append('{')
          .append("\"x\":")
          .append(h.x())
          .append(',')
          .append("\"y\":")
          .append(fmt(h.y()))
          .append(',')
          .append("\"r\":")
          .append(h.r())
          .append(',')
          .append("\"hit\":")
          .append(h.hit())
          .append(',')
          .append("\"ts\":")
          .append(esc(h.ts()))
          .append(',')
          .append("\"sid\":")
          .append(esc(h.sid()))
          .append('}');
    }
    sb.append("]}");
    return sb.toString();
  }

  public static String error(int code, String message) {
    return "{\"ok\":false,\"error\":" + esc(message) + ",\"code\":" + code + "}";
  }

  private static String fmt(double v) {
    return String.format(java.util.Locale.US, "%.10g", v);
  }
}
