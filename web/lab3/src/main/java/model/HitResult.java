package model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "hit_results")
public class HitResult {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private double x;
  private double y;
  private double r;
  private boolean hit;

  @Column(nullable = false)
  private long exec_micros;

  @Column(nullable = false)
  private OffsetDateTime created_at = OffsetDateTime.now();

  public HitResult() {}

  public HitResult(double x, double y, double r, boolean hit, long exec_micros) {
    this.x = x;
    this.y = y;
    this.r = r;
    this.hit = hit;
    this.exec_micros = exec_micros;
  }

  public Long getId() {
    return id;
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

  public long getExecMicros() {
    return exec_micros;
  }

  public OffsetDateTime getCreated_at() {
    return created_at;
  }

  @Transient
  public java.util.Date getCreatedAtDate() {
    return created_at == null ? null : java.util.Date.from(created_at.toInstant());
  }
}
