package app.controller;

import app.dto.point.PointRequest;
import app.dto.point.PointResponse;
import app.entity.PointEntity;
import app.entity.UserEntity;
import app.security.CustomUserDetails;
import app.service.PointService;
import app.service.UserService;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

  private final PointService pointService;
  private final UserService userService;

  @GetMapping
  public ResponseEntity<List<PointResponse>> getPoints(Authentication authentication) {
    UserEntity user = getCurrentUser(authentication);

    List<PointEntity> entities = pointService.getPointsForUser(user);

    List<PointResponse> responses =
        entities.stream()
            .map(
                p ->
                    new PointResponse(
                        p.getId(),
                        p.getX(),
                        p.getY(),
                        p.getR(),
                        p.getIsHit(),
                        p.getNowTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        p.getWorkTime()))
            .toList();

    return ResponseEntity.ok(responses);
  }

  @PostMapping
  public ResponseEntity<PointResponse> addPoint(
      @RequestBody PointRequest request, Authentication authentication) {
    UserEntity user = getCurrentUser(authentication);

    PointEntity saved = pointService.addPoint(user, request.getX(), request.getY(), request.getR());

    PointResponse response =
        new PointResponse(
            saved.getId(),
            saved.getX(),
            saved.getY(),
            saved.getR(),
            saved.getIsHit(),
            saved.getNowTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            saved.getWorkTime());

    return ResponseEntity.ok(response);
  }

  private UserEntity getCurrentUser(Authentication authentication) {
    if (authentication == null
        || !(authentication.getPrincipal() instanceof CustomUserDetails custom)) {
      throw new IllegalStateException("Authentication not found");
    }

    Long userId = custom.getId();

    return UserEntity.builder()
        .id(userId)
        .username(custom.getUsername())
        .passwordHash(custom.getPassword())
        .build();
  }
}
