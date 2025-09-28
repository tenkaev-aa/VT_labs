package main;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

final class RequestContext {
  final String method;
  final Map<String, String> query;
  final String sid;
  public static final String SID_PARAM = "sid";
  public static final String DEFAULT_METHOD = "GET";
  public static final String ANON = "anonymous";

  private RequestContext(String method, Map<String, String> query, String sid) {
    this.method = method;
    this.query = query;
    this.sid = sid;
  }

  static RequestContext from(Properties env) {
    String method = env.getProperty("REQUEST_METHOD", DEFAULT_METHOD);
    String qs = env.getProperty("QUERY_STRING", "");
    Map<String, String> q = parseQuery(qs);
    String sid = q.getOrDefault(SID_PARAM, ANON).trim();
    if (sid.isEmpty()) sid = ANON;
    return new RequestContext(method, q, sid);
  }

  boolean isGet() {
    return DEFAULT_METHOD.equalsIgnoreCase(method);
  }

  private static Map<String, String> parseQuery(String qs) {
    Map<String, String> m = new LinkedHashMap<>();
    if (qs == null || qs.isEmpty()) return m;
    for (String p : qs.split("&")) {
      int k = p.indexOf('=');
      if (k >= 0) m.put(urlDecode(p.substring(0, k)), urlDecode(p.substring(k + 1)));
      else m.put(urlDecode(p), "");
    }
    return m;
  }

  private static String urlDecode(String s) {
    try {
      return java.net.URLDecoder.decode(s, java.nio.charset.StandardCharsets.UTF_8);
    } catch (Exception e) {
      return s;
    }
  }
}
