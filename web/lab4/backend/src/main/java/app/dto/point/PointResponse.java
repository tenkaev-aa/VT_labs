package app.dto.point;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PointResponse {

  private Long id;
  private Integer x;
  private Double y;
  private Integer r;
  private Boolean isHit;
  private String nowTime;
  private Double workTime;
}
