package model;

import enums.Climate;
import enums.Government;
import enums.StandardOfLiving;
import input_object.FieldInput;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import util.DateUtils;

/**
 * Класс, представляющий город. Реализует интерфейс {@link Comparable}, что позволяет сортировать
 * объекты {@link City}.
 */
@Entity
@Table(name = "cities")
public class City implements Comparable<City>, Serializable {

  @Serial private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false)
  private String name;

  @Embedded private Coordinates coordinates;

  @Column(name = "creation_date", nullable = false)
  private LocalDateTime creationDate;

  @Column(nullable = false)
  private Long area;

  @Column(nullable = false)
  private Integer population;

  @Column(name = "meters_above_sea_level")
  private Float metersAboveSeaLevel;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Climate climate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Government government;

  @Enumerated(EnumType.STRING)
  @Column(name = "standard_of_living", nullable = false)
  private StandardOfLiving standardOfLiving;

  @Embedded private Human governor;

  @Column(name = "owner_id", nullable = false)
  private int ownerId;

  public City() {}

  public City(
      Integer id,
      String name,
      Coordinates coordinates,
      LocalDateTime creationDate,
      Long area,
      Integer population,
      Float metersAboveSeaLevel,
      Climate climate,
      Government government,
      StandardOfLiving standardOfLiving,
      Human governor) {
    this.id = id;
    this.name = name;
    this.coordinates = coordinates;
    this.creationDate = creationDate;
    this.area = area;
    this.population = population;
    this.metersAboveSeaLevel = metersAboveSeaLevel;
    this.climate = climate;
    this.government = government;
    this.standardOfLiving = standardOfLiving;
    this.governor = governor;
  }

  /**
   * @return Уникальный идентификатор города.
   */
  public int getId() {
    return id;
  }

  /**
   * @return Название города.
   */
  public String getName() {
    return name;
  }

  /**
   * @return Координаты города.
   */
  public Coordinates getCoordinates() {
    return coordinates;
  }

  /**
   * @return Дата создания города.
   */
  public LocalDateTime getCreationDate() {
    return creationDate;
  }

  /**
   * @return Площадь города.
   */
  public Long getArea() {
    return area;
  }

  /**
   * @return Население города.
   */
  public Integer getPopulation() {
    return population;
  }

  /**
   * @return Высота над уровнем моря (может быть null).
   */
  public Float getMetersAboveSeaLevel() {
    return metersAboveSeaLevel;
  }

  /**
   * @return Климат города.
   */
  public Climate getClimate() {
    return climate;
  }

  /**
   * @return Форма правления в городе.
   */
  public Government getGovernment() {
    return government;
  }

  /**
   * @return Уровень жизни в городе.
   */
  public StandardOfLiving getStandardOfLiving() {
    return standardOfLiving;
  }

  /**
   * @return Губернатор города (может быть null).
   */
  public Human getGovernor() {
    return governor;
  }

  /**
   * Устанавливает ID города.
   *
   * @param id Новый идентификатор.
   */
  public void setId(int id) {
    this.id = id;
  }

  public int getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(int ownerId) {
    this.ownerId = ownerId;
  }

  /**
   * Устанавливает название города.
   *
   * @param name Новое название города.
   */
  @FieldInput(prompt = "Название города:", flags = 0b010)
  public void setName(String name) {
    this.name = name;
  }

  @FieldInput(prompt = "Координаты города:", flags = 0b010)
  public void setCoordinates(Coordinates coordinates) {
    this.coordinates = coordinates;
  }

  public void setCreationDate(LocalDateTime creationDate) {
    this.creationDate = creationDate;
  }

  @FieldInput(prompt = "Площадь города:", flags = 0b011)
  public void setArea(Long area) {
    this.area = area;
  }

  @FieldInput(prompt = "Высота над уровнем моря(можно оставить пустой):")
  public void setMetersAboveSeaLevel(Float metersAboveSeaLevel) {
    this.metersAboveSeaLevel = metersAboveSeaLevel;
  }

  @FieldInput(prompt = "Климат города:", flags = 0b010)
  public void setClimate(Climate climate) {
    this.climate = climate;
  }

  @FieldInput(prompt = "Губернатор(можно оставить пустым):")
  public void setGovernor(Human governor) {
    this.governor = governor;
  }

  @FieldInput(prompt = "Уровень жизни города:", flags = 0b010)
  public void setStandardOfLiving(StandardOfLiving standardOfLiving) {
    this.standardOfLiving = standardOfLiving;
  }

  @FieldInput(prompt = "Популяция города:", flags = 0b011)
  public void setPopulation(Integer population) {
    this.population = population;
  }

  @FieldInput(prompt = "Правительство города:", flags = 0b010)
  public void setGovernment(Government government) {
    this.government = government;
  }

  /**
   * Возвращает строковое представление объекта {@link City}.
   *
   * @return Строка с информацией о городе.
   */
  @Override
  public String toString() {
    return "City{"
        + "id="
        + id
        + ", name='"
        + name
        + '\''
        + ", coordinates="
        + coordinates
        + ", creationDate="
        + DateUtils.formatDateTime(creationDate)
        + ", area="
        + area
        + ", population="
        + population
        + ", metersAboveSeaLevel="
        + metersAboveSeaLevel
        + ", climate="
        + climate
        + ", government="
        + government
        + ", standardOfLiving="
        + standardOfLiving
        + ", governor="
        + governor
        + '}';
  }

  /**
   * Сравнивает данный город с другим городом на основе определенных критериев. Использует {@link
   * CityComparator} для сравнения.
   *
   * @param other Другой объект {@link City} для сравнения.
   * @return Результат сравнения (отрицательное число, 0 или положительное число).
   */
  @Override
  public int compareTo(City other) {
    return new CityComparator().compare(this, other);
  }
}
