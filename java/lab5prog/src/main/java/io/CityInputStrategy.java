package io;

import builder.Builder;
import city.City;
import city.Coordinates;
import city.Human;
import data.DataReader;
import enums.Climate;
import enums.Government;
import enums.StandardOfLiving;
import java.io.IOException;
import java.util.Optional;
import util.DateUtils;
import util.IdGenerator;

public class CityInputStrategy implements InputStrategy<City> {
  private final DataReader reader;

  public CityInputStrategy(DataReader reader) {
    this.reader = reader;
  }

  @Override
  public City inputObject() throws IOException {
    Builder<City> builder = new Builder<>(new City());
    return builder
        .set(City::setId, IdGenerator.getAndIncrement())
        .set(City::setCreationDate, DateUtils.getCurrentDateTime())
        .set(
            City::setName,
            reader
                .readString("Название: ", "Название не может быть пустым", false)
                .orElseThrow(() -> new IllegalArgumentException("Название обязательно")))
        .set(City::setCoordinates, readCoordinates())
        .set(
            City::setArea,
            reader
                .readLong("Площадь: ", "Площадь должна быть положительной", true, false)
                .orElseThrow(() -> new IllegalArgumentException("Площадь обязательна")))
        .set(
            City::setPopulation,
            reader
                .readInt("Население: ", "Население должно быть положительным", true, false)
                .orElseThrow(() -> new IllegalArgumentException("Население обязательно")))
        .set(
            City::setMetersAboveSeaLevel,
            reader
                .readFloat("Высота над уровнем моря (можно оставить пустым): ", null, false, true)
                .orElse(null) //
            )
        .set(
            City::setClimate,
            reader
                .readEnum("Выберите климат: ", Climate.class, "Некорректный климат", false)
                .orElseThrow(() -> new IllegalArgumentException("Климат обязателен")))
        .set(
            City::setGovernment,
            reader
                .readEnum(
                    "Выберите тип правительства: ",
                    Government.class,
                    "Некорректное правительство",
                    false)
                .orElseThrow(() -> new IllegalArgumentException("Тип правительства обязателен")))
        .set(
            City::setStandardOfLiving,
            reader
                .readEnum(
                    "Выберите уровень жизни: ",
                    StandardOfLiving.class,
                    "Некорректный уровень жизни",
                    false)
                .orElseThrow(() -> new IllegalArgumentException("Уровень жизни обязателен")))
        .set(City::setGovernor, readNullableHuman().orElse(null))
        .build();
  }

  private Coordinates readCoordinates() throws IOException {
    double x = readXCoordinate();
    int y = readYCoordinate();
    return new Coordinates(x, y);
  }

  private double readXCoordinate() throws IOException {
    Optional<Double> xOpt =
        reader.readDouble(
            "Координата X (максимум 36): ", "Значение должно быть не больше 36", false, false);

    if (xOpt.isPresent() && xOpt.get() <= 36) {
      return xOpt.get();
    } else {
      System.out.println("Ошибка: Значение координаты X должно быть не больше 36.");
      return readXCoordinate();
    }
  }

  private int readYCoordinate() throws IOException {
    Optional<Integer> yOpt =
        reader.readInt("Координата Y: ", "Введите корректное число", false, false);

    if (yOpt.isPresent()) {
      return yOpt.get();
    } else {
      System.out.println("Ошибка: Координата Y обязательна. Пожалуйста, попробуйте снова.");
      return readYCoordinate();
    }
  }

  private Optional<Human> readNullableHuman() throws IOException {
    Optional<Float> height =
        reader.readFloat("Рост губернатора (можно оставить пустым): ", null, false, true);
    return height.map(Human::new);
  }
}
