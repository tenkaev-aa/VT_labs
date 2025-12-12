package app.repository;

import app.entity.PointEntity;
import app.entity.UserEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointRepository extends JpaRepository<PointEntity, Long> {

  List<PointEntity> findAllByUserOrderByIdAsc(UserEntity user);
}
