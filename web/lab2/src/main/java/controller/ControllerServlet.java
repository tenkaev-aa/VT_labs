package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ControllerServlet extends HttpServlet {

  private static final String INDEX_JSP = "/WEB-INF/jsp/index.jsp";
  private static final String AREA_CHECK_PATH = "/area-check";

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    req.getRequestDispatcher(INDEX_JSP).forward(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    if (hasXYR(req)) {
      forward(req, resp, AREA_CHECK_PATH);
    } else {
      forward(req, resp, INDEX_JSP);
    }
  }

  private boolean hasXYR(HttpServletRequest req) {
    String x = req.getParameter("x");
    String y = req.getParameter("y");
    String[] r = req.getParameterValues("r");
    return x != null
        && !x.isBlank()
        && y != null
        && !y.isBlank()
        && r != null
        && r.length == 1
        && r[0] != null
        && !r[0].isBlank();
  }

  private void forward(HttpServletRequest req, HttpServletResponse resp, String path)
      throws ServletException, IOException {
    RequestDispatcher rd = req.getRequestDispatcher(path);
    rd.forward(req, resp);
  }
}
