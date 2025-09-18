package main;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

final class RequestContext {
  final String method;
  final Map<String,String> query;
  final String sid;

  private RequestContext(String method, Map<String,String> query, String sid) {
    this.method = method; this.query = query; this.sid = sid;
  }

  static RequestContext from(Properties env) {
    String method = env.getProperty("REQUEST_METHOD", "GET");
    String qs = env.getProperty("QUERY_STRING", "");
    Map<String,String> q = parseQuery(qs);
    String sid = q.getOrDefault("sid", "anonymous").trim();
    if (sid.isEmpty()) sid = "anonymous";
    return new RequestContext(method, q, sid);
  }

  boolean isGet() { return "GET".equalsIgnoreCase(method); }

  private static Map<String,String> parseQuery(String qs) {
    Map<String,String> m = new LinkedHashMap<>();
    if (qs == null || qs.isEmpty()) return m;
    for (String p : qs.split("&")) {
      int k = p.indexOf('=');
      if (k >= 0) m.put(urlDecode(p.substring(0,k)), urlDecode(p.substring(k+1)));
      else m.put(urlDecode(p), "");
    }
    return m;
  }
  private static String urlDecode(String s) {
    try { return java.net.URLDecoder.decode(s, java.nio.charset.StandardCharsets.UTF_8); }
    catch (Exception e) { return s; }
  }
}