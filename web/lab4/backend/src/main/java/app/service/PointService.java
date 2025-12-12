package app.service;

import app.entity.PointEntity;
import app.entity.UserEntity;
import app.repository.PointRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

  private final PointRepository pointRepository;

  public PointEntity addPoint(UserEntity user, int x, double y, int r) {
    long start = System.nanoTime();

    boolean isHit = checkHit(x, y, r);

    long end = System.nanoTime();
    double workTimeMs = (end - start) / 1_000_000.0;

    PointEntity point =
        PointEntity.builder()
            .x(x)
            .y(y)
            .r(r)
            .isHit(isHit)
            .nowTime(LocalDateTime.now())
            .workTime(workTimeMs)
            .user(user)
            .build();

    return pointRepository.save(point);
  }

  public List<PointEntity> getPointsForUser(UserEntity user) {
    return pointRepository.findAllByUserOrderByIdAsc(user);
  }

  private boolean checkHit(double x, double y, double r) {

    boolean inTriangle = (x >= 0 && y >= 0) && (x <= r) && (y <= (-0.5 * x + r / 2.0));

    boolean inRectangle = (x <= 0 && y >= 0) && (x >= -r) && (y <= r);

    boolean inCircle = (x <= 0 && y <= 0) && (x * x + y * y <= r * r);

    return inTriangle || inRectangle || inCircle;
  }
}
