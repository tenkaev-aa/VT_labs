package storage;

import java.io.Serializable;
import model.City;
import util.DateUtils;

public class CityWithOwnerName implements Serializable {
  private final City city;
  private final String ownerName;

  public CityWithOwnerName(City city, String ownerName) {
    this.city = city;
    this.ownerName = ownerName;
  }

  public City getCity() {
    return city;
  }

  public String getOwnerName() {
    return ownerName;
  }

  @Override
  public String toString() {
    City c = city;
    return String.format(
        """
      Город: %s (ID: %d)
        Координаты: (%.2f, %d)
        Дата создания: %s
        Площадь: %d
        Население: %d
        Высота над уровнем моря: %s
        Климат: %s
        Правительство: %s
        Уровень жизни: %s
        Губернатор: %s
        Владелец: %s
      """,
        c.getName(),
        c.getId(),
        c.getCoordinates().getX(),
        c.getCoordinates().getY(),
        DateUtils.formatDateTime(c.getCreationDate()),
        c.getArea(),
        c.getPopulation(),
        c.getMetersAboveSeaLevel() != null ? c.getMetersAboveSeaLevel() : "не указана",
        c.getClimate(),
        c.getGovernment(),
        c.getStandardOfLiving(),
        c.getGovernor() != null ? c.getGovernor().getHeight() + " м" : "отсутствует",
        ownerName);
  }
}
