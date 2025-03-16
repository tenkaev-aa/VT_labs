package io;
import city.City;

import city.Coordinates;
import city.Human;
import data.DataReader;
import enums.Climate;
import enums.Government;
import enums.StandardOfLiving;
import builder.Builder;
import util.DateUtils;
import util.IdGenerator;

import java.io.IOException;
import java.util.Optional;

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
            .set(City::setName, reader.readString("Название: ", "Название не может быть пустым"))
            .set(City::setCoordinates, readCoordinates())
            .set(City::setArea, reader.readLong("Площадь: ", "Площадь должна быть положительной", true))
            .set(City::setPopulation, reader.readInt("Население: ", "Население должно быть положительным", true))
            .set(City::setMetersAboveSeaLevel, reader.readFloat("Высота над уровнем моря (можно оставить пустым): ", null, false))
            .set(City::setClimate, reader.readEnum("Выберите климат: ", Climate.class, "Некорректный климат"))
            .set(City::setGovernment, reader.readEnum("Выберите тип правительства: ", Government.class, "Некорректное правительство"))
            .set(City::setStandardOfLiving, reader.readEnum("Выберите уровень жизни: ", StandardOfLiving.class, "Некорректный уровень жизни"))
            .set(City::setGovernor, readNullableHuman().orElse(null))
            .build();
  }

  private Coordinates readCoordinates() throws IOException {
    double x = reader.readDouble("Координата X (максимум 36): ", "Значение должно быть не больше 36", true);
    int y = reader.readInt("Координата Y: ", "Введите корректное число", true);
    return new Coordinates(x, y);
  }

  private Optional<Human> readNullableHuman() throws IOException {
    Float height = reader.readFloat("Рост губернатора (можно оставить пустым): ", null, false);
    return Optional.ofNullable(height).map(Human::new);
  }
}