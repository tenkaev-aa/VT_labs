package core;

public final class HitTester {
  private HitTester() {}

  public static boolean isHit(double x, double y, int r) {
    return inQuarterCircle(x, y, r) || inRectangle(x, y, r) || inTriangle(x, y, r);
  }

  private static boolean inQuarterCircle(double x, double y, int r) {
    return x <= 0 && y >= 0 && (x * x + y * y) <= (long) r * r + 1e-12;
  }

  private static boolean inRectangle(double x, double y, int r) {
    return x >= 0 && y <= 0 && x <= r + 1e-12 && y >= -r / 2.0 - 1e-12;
  }

  private static boolean inTriangle(double x, double y, int r) {

    if (!(x <= 0 && y <= 0 && x >= -r / 2.0 && y >= -r / 2.0)) return false;
    return (x + y) >= -r / 2.0 - 1e-12;
  }
}
