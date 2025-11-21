package service;

public final class HitTester {
  private HitTester() {}
  public static boolean isHit(double x, double y, double r) {
   //прямоугольник
    if (x >= 0 && y >= 0 && x <= r && y <= r) return true;
    //треугольник
    if (x <= 0 && y >= 0 && x >= -r/2 && y <= r && (y <= (2.0*r + 2.0*x))) return true;
    // круг
    if (x >= 0 && y <= 0 && x*x + y*y <= r*r + 1e-12) return true;
    return false;
  }
}
