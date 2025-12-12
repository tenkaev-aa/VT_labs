package app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "points")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Integer x;

  @Column(nullable = false)
  private Double y;

  @Column(nullable = false)
  private Integer r;

  @Column(name = "is_hit", nullable = false)
  private Boolean isHit;

  @Column(name = "now_time", nullable = false, length = 100)
  private LocalDateTime nowTime;

  @Column(name = "work_time", nullable = false)
  private Double workTime;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;
}
