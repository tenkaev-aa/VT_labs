package city;

import enums.Climate;
import enums.Government;
import enums.StandardOfLiving;
import java.time.LocalDateTime;
import util.DateUtils;
import validation.validationService;

/**
 * Класс, представляющий город. Реализует интерфейс {@link Comparable}, что позволяет сортировать
 * объекты {@link City}.
 */
public class City implements Comparable<City> {
  private Integer id;
  private String name;
  private final Coordinates coordinates;
  private final LocalDateTime creationDate;
  private final Long area;
  private Integer population;
  private final Float metersAboveSeaLevel;
  private final Climate climate;
  private Government government;
  private StandardOfLiving standardOfLiving;
  private Human governor;

  /**
   * Конструктор для создания объекта {@link City}.
   *
   * @param name Название города.
   * @param coordinates Координаты города.
   * @param creationDate Дата создания.
   * @param area Площадь города.
   * @param population Население города.
   * @param metersAboveSeaLevel Высота над уровнем моря (может быть null).
   * @param climate Климат города.
   * @param government Форма правления города.
   * @param standardOfLiving Уровень жизни.
   * @param governor Губернатор города (может быть null).
   */
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
    this.creationDate = creationDate;
    this.name = validationService.validateName(name);
    this.coordinates = validationService.validateCoordinates(coordinates);
    this.area = validationService.validateArea(area);
    this.population = validationService.validatePopulation(population);
    this.metersAboveSeaLevel = metersAboveSeaLevel;
    this.climate = validationService.validateClimate(climate);
    this.government = validationService.validateGovernment(government);
    this.standardOfLiving = validationService.validateStandardOfLiving(standardOfLiving);
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

  /**
   * Устанавливает название города.
   *
   * @param name Новое название города.
   */
  public void setName(String name) {
    this.name = name;
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
