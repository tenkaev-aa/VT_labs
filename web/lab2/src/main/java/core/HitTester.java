package core;

public final class HitTester {
  private HitTester() {}

  public static boolean isHit(double x, double y, double r) {
    final double eps = 1e-12;
    return inSquareI(x, y, r, eps)
        || inQuarterCircleII(x, y, r, eps)
        || inRightTriangleIV(x, y, r, eps);
  }

  private static boolean inSquareI(double x, double y, double r, double eps) {
    return x >= -eps && y >= -eps && x <= r + eps && y <= r + eps;
  }

  private static boolean inQuarterCircleII(double x, double y, double r, double eps) {
    if (x > eps || y < -eps) return false;
    double rr = (r * r) / 4.0;
    return x * x + y * y <= rr + eps;
  }

  private static boolean inRightTriangleIV(double x, double y, double r, double eps) {
    if (x < -eps || y > eps) return false;
    return y + eps >= x - r;
  }
}
