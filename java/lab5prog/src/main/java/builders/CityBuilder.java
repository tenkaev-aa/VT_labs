package builders;

import city.City;
import city.Coordinates;
import city.Human;
import enums.Climate;
import enums.Government;
import enums.StandardOfLiving;
import java.time.LocalDateTime;
import util.DateUtils;
import util.IdGenerator;

public class CityBuilder extends Builder<City> {
  private String name;
  private Coordinates coordinates;
  private Long area;
  private Integer population;
  private Float metersAboveSeaLevel;
  private Climate climate;
  private Government government;
  private StandardOfLiving standardOfLiving;
  private Human governor;
  private final LocalDateTime creationDate = DateUtils.getCurrentDateTime();
  private final Integer id = IdGenerator.getAndIncrement();

  @Override
  public Builder<City> setName(String name) {
    this.name = name;
    return this;
  }

  @Override
  public Builder<City> setNumber(Number value) {
    if (value instanceof Long) {
      this.area = (Long) value;
    } else if (value instanceof Integer) {
      this.population = (Integer) value;
    } else if (value instanceof Float) {
      this.metersAboveSeaLevel = (Float) value;
    }
    return this;
  }

  @Override
  public Builder<City> setEnum(Enum<?> value) {
    if (value instanceof Climate) {
      this.climate = (Climate) value;
    } else if (value instanceof Government) {
      this.government = (Government) value;
    } else if (value instanceof StandardOfLiving) {
      this.standardOfLiving = (StandardOfLiving) value;
    }
    return this;
  }

  @Override
  public Builder<City> setNestedObject(Object value) {
    if (value instanceof Coordinates) {
      this.coordinates = (Coordinates) value;
    } else if (value instanceof Human) {
      this.governor = (Human) value;
    }
    return this;
  }

  @Override
  public City build() {
    return new City(
        id,
        name,
        coordinates,
        creationDate,
        area,
        population,
        metersAboveSeaLevel,
        climate,
        government,
        standardOfLiving,
        governor);
  }
}
