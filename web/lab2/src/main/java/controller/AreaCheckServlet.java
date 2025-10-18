package controller;

import core.HitAttempt;
import core.HitTester;
import core.Validation;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AreaCheckServlet extends HttpServlet {

  private static final String INDEX_JSP = "/WEB-INF/jsp/index.jsp";
  private static final String RESULT_JSP = "/WEB-INF/jsp/result.jsp";
  private static final String SESSION_HITS_ATTR = "hits";

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    long t0 = System.nanoTime();

    Validation.Result vr = Validation.validate(req);
    if (!vr.ok()) {
      req.setAttribute("error", "Некорректные параметры.");
      req.setAttribute("errors", vr.errors());
      req.getRequestDispatcher(INDEX_JSP).forward(req, resp);
      return;
    }

    double x = vr.x();
    double y = vr.y();
    double r = vr.r();

    boolean hit = HitTester.isHit(x, y, r);
    long execMicros = (System.nanoTime() - t0) / 1_000;
    double execMs = execMicros / 1000.0;
    long nowMillis = System.currentTimeMillis();

    HitAttempt attempt = new HitAttempt(x, y, r, hit, nowMillis, execMicros);

    HttpSession session = req.getSession();
    List<HitAttempt> hits = (List<HitAttempt>) session.getAttribute(SESSION_HITS_ATTR);
    if (hits == null) {
      hits = new ArrayList<>();
      session.setAttribute(SESSION_HITS_ATTR, hits);
    }
    hits.add(attempt);

    String serverTime =
        ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    session.setAttribute("lastExecMs", execMs);
    session.setAttribute("lastServerTime", serverTime);

    req.setAttribute("attempt", attempt);
    req.setAttribute("execMicros", execMicros);

    req.getRequestDispatcher(RESULT_JSP).forward(req, resp);
  }
}
