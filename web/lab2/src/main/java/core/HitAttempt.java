package core;

public class HitAttempt {
  private final double x, y, r;
  private final boolean hit;
  private final long timestampMillis;
  private final long execMicros;

  public HitAttempt(double x, double y, double r, boolean hit, long ts, long execMicros) {
    this.x = x;
    this.y = y;
    this.r = r;
    this.hit = hit;
    this.timestampMillis = ts;
    this.execMicros = execMicros;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getR() {
    return r;
  }

  public boolean isHit() {
    return hit;
  }

  public java.util.Date getTimestamp() {
    return new java.util.Date(timestampMillis);
  }

  public long getExecMicros() {
    return execMicros;
  }
}
