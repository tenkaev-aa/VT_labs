package core;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

public final class Validation {
  private Validation() {}

  private static final Set<Integer> X_ALLOWED = Set.of(-4, -3, -2, -1, 0, 1, 2, 3, 4);
  private static final double[] R_ALLOWED = {1, 1.5, 2, 2.5, 3};

  public static final class Result {
    private final Integer x;
    private final Double y;
    private final Double r;
    private final Map<String, String> errors;

    private Result(Integer x, Double y, Double r, Map<String, String> errors) {
      this.x = x;
      this.y = y;
      this.r = r;
      this.errors = errors;
    }

    public boolean ok() {
      return errors.isEmpty();
    }

    public Integer x() {
      return x;
    }

    public Double y() {
      return y;
    }

    public Double r() {
      return r;
    }

    public Map<String, String> errors() {
      return errors;
    }
  }

  public static Result validate(HttpServletRequest req) {
    String sx = req.getParameter("x");
    String sy = req.getParameter("y");
    String[] rs = req.getParameterValues("r");

    Map<String, String> err = new LinkedHashMap<>();

    Optional<Integer> xOpt = parseX(sx);
    if (sx == null || sx.isBlank()) {
      err.put("x", "Параметр X не задан.");
    } else if (xOpt.isEmpty()) {
      err.put("x", "X должен быть одним из {-4,-3,-2,-1,0,1,2,3,4}.");
    }

    Optional<Double> yOpt = parseY(sy);
    if (sy == null || sy.isBlank()) {
      err.put("y", "Параметр Y не задан.");
    } else if (yOpt.isEmpty()) {
      err.put("y", "Y должен быть числом в интервале (-3; 3).");
    }

    Optional<Double> rOpt = parseR(rs);
    if (rs == null || rs.length == 0) {
      err.put("r", "Выберите одно значение R.");
    } else if (rs.length > 1) {
      err.put("r", "Можно выбрать только одно значение R.");
    } else if (rOpt.isEmpty()) {
      err.put("r", "R должен быть одним из {1, 1.5, 2, 2.5, 3}.");
    }

    return new Result(xOpt.orElse(null), yOpt.orElse(null), rOpt.orElse(null), err);
  }

  public static Optional<Integer> parseX(String s) {
    try {
      int v = Integer.parseInt(trimmedNonEmpty(s));
      return X_ALLOWED.contains(v) ? Optional.of(v) : Optional.empty();
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  private static Optional<Double> parseY(String s) {
    try {
      String t = trimmedNonEmpty(s);
      if (t.length() > 32) return Optional.empty();
      double v = Double.parseDouble(t.replace(',', '.'));
      if (!Double.isFinite(v)) return Optional.empty();
      return (v > -3.0 && v < 3.0) ? Optional.of(v) : Optional.empty();
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  private static Optional<Double> parseR(String[] rs) {
    if (rs == null || rs.length != 1) return Optional.empty();
    try {
      double v = Double.parseDouble(trimmedNonEmpty(rs[0]).replace(',', '.'));
      for (double a : R_ALLOWED) if (Math.abs(a - v) < 1e-9) return Optional.of(a);
      return Optional.empty();
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  private static String trimmedNonEmpty(String s) {
    if (s == null) throw new IllegalArgumentException("null");
    String t = s.trim();
    if (t.isEmpty()) throw new IllegalArgumentException("blank");
    return t;
  }
}
