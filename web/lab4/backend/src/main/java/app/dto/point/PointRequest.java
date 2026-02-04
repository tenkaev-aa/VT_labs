package app.dto.point;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PointRequest {
  private Integer x;
  private Double y;
  private Integer r;
}
