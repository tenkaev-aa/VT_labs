package io;
import util.DateUtils;
import util.IdGenerator;
import builders.Builder;
import city.City;
import city.Coordinates;
import city.Human;
import enums.Climate;
import enums.Government;
import enums.StandardOfLiving;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

public class CityInputStrategy implements InputStrategy<City> {
  @Override
  public City inputObject(Scanner scanner) throws IOException {
    Builder<City> builder = new Builder<>(new City());
    return builder
            .set(City::setId, IdGenerator.getAndIncrement())
            .set(City::setCreationDate, DateUtils.getCurrentDateTime())
            .set(City::setName, inputString(scanner, "Название: ", "Название не может быть пустым"))
            .set(City::setCoordinates, inputCoordinates(scanner))
            .set(City::setArea, inputNumber(scanner, "Площадь: ", "Площадь должна быть положительной", Long::parseLong, true).orElseThrow(() -> new IllegalArgumentException("Площадь не может быть пустой")))
            .set(City::setPopulation, inputNumber(scanner, "Население: ", "Население должно быть положительным", Integer::parseInt, true).orElseThrow(() -> new IllegalArgumentException("Население не может быть пустым")))
            .set(City::setMetersAboveSeaLevel, inputNumber(scanner, "Высота над уровнем моря (можно оставить пустым): ", null, Float::parseFloat, false).orElse(null))
            .set(City::setClimate, inputEnum(scanner, "Выберите климат", Climate.class))
            .set(City::setGovernment, inputEnum(scanner, "Выберите тип правительства", Government.class))
            .set(City::setStandardOfLiving, inputEnum(scanner, "Выберите уровень жизни", StandardOfLiving.class))
            .set(City::setGovernor, inputNullableHuman(scanner).orElse(null))
            .build();
  }

  public City createFromArgs(BufferedReader reader) throws IOException {
    Builder<City> builder = new Builder<>(new City());
    return builder
            .set(City::setId, IdGenerator.getAndIncrement())
            .set(City::setCreationDate, DateUtils.getCurrentDateTime())
            .set(City::setName, readString(reader, "Ошибка: название не может быть пустым"))
            .set(City::setCoordinates, readCoordinates(reader))
            .set(City::setArea, readNumber(reader, Long::parseLong, "Ошибка: площадь должна быть положительным числом", true)
                    .orElseThrow(() -> new IllegalArgumentException("Площадь не может быть пустой")))
            .set(City::setPopulation, readNumber(reader, Integer::parseInt, "Ошибка: население должно быть положительным числом", true)
                    .orElseThrow(() -> new IllegalArgumentException("Население не может быть пустым")))
            .set(City::setMetersAboveSeaLevel, readNumber(reader, Float::parseFloat, null, false).orElse(null))
            .set(City::setClimate, readEnum(reader, Climate.class, "Ошибка: некорректный климат"))
            .set(City::setGovernment, readEnum(reader, Government.class, "Ошибка: некорректное правительство"))
            .set(City::setStandardOfLiving, readEnum(reader, StandardOfLiving.class, "Ошибка: некорректный уровень жизни"))
            .set(City::setGovernor, readNullableHuman(reader).orElse(null))
            .build();
  }

  private String readString(BufferedReader reader, String errorMessage) throws IOException {
    String line;
    while ((line = reader.readLine()) != null) {
      line = line.trim();
      if (!line.isEmpty()) return line;
      System.out.println(errorMessage);
    }
    throw new IllegalArgumentException("Ошибка: достигнут конец ввода");
  }
  private Coordinates readCoordinates(BufferedReader reader) throws IOException {
    double x = readNumber(reader, Double::parseDouble, "Ошибка: координата X должна быть числом", true,36.0)
            .orElseThrow(() -> new IllegalArgumentException("Координата X не может быть пустой"));
    int y = readNumber(reader, Integer::parseInt, "Ошибка: координата Y должна быть числом", true)
            .orElseThrow(() -> new IllegalArgumentException("Координата Y не может быть пустой"));
    return new Coordinates(x, y);
  }
  private Optional<Human> readNullableHuman(BufferedReader reader) throws IOException {
    return readNumber(reader, Float::parseFloat, null, false).map(Human::new);
  }

  private String inputString(Scanner scanner, String prompt, String errorMessage) {
    while (true) {
      System.out.print(prompt);
      String input = scanner.nextLine().trim();
      if (!input.isEmpty()) return input;
      System.out.println("Ошибка: " + errorMessage);
    }
  }

  private Coordinates inputCoordinates(Scanner scanner) {
    double x =
        inputNumber(
                scanner,
                "Координата X (максимум 36): ",
                "Значение должно быть не больше 36",
                Double::parseDouble,
                false,
                36.0)
            .orElseThrow(() -> new IllegalArgumentException("Координата X не может быть пустой"));
    int y =
        inputNumber(scanner, "Координата Y: ", "Введите корректное число", Integer::parseInt, false)
            .orElseThrow(() -> new IllegalArgumentException("Координата Y не может быть пустой"));
    return new Coordinates(x, y);
  }

  private Optional<Human> inputNullableHuman(Scanner scanner) {
    return inputNumber(
            scanner, "Рост губернатора (можно оставить пустым): ", null, Float::parseFloat, true)
        .map(Human::new);
  }

  private <T extends Number> Optional<T> inputNumber(
      Scanner scanner,
      String prompt,
      String errorMessage,
      Parser<T> parser,
      boolean mustBePositive) {
    return inputNumber(scanner, prompt, errorMessage, parser, mustBePositive, null);
  }

  private <T extends Number> Optional<T> inputNumber(
      Scanner scanner,
      String prompt,
      String errorMessage,
      Parser<T> parser,
      boolean mustBePositive,
      Double maxValue) {
    while (true) {
      System.out.print(prompt);
      String input = scanner.nextLine().trim();
      if (input.isEmpty() && errorMessage == null) return Optional.empty();
      try {
        T value = parser.parse(input);
        if ((!mustBePositive || value.doubleValue() > 0)
            && (maxValue == null || value.doubleValue() <= maxValue)) {
          return Optional.of(value);
        }
      } catch (NumberFormatException ignored) {
      }
      System.out.println(
          "Ошибка: " + (errorMessage != null ? errorMessage : "Введите корректное число."));
    }
  }

  private <T extends Enum<T>> T inputEnum(Scanner scanner, String prompt, Class<T> enumClass)
      throws IOException {
    return readEnumHelper(scanner::nextLine, prompt, enumClass);
  }

  private <T extends Enum<T>> T readEnum(
      BufferedReader reader, Class<T> enumClass, String errorMessage) throws IOException {
    return readEnumHelper(reader::readLine, errorMessage, enumClass);
  }

  private <T extends Enum<T>> T readEnumHelper(
      ReaderFunction reader, String prompt, Class<T> enumClass) throws IOException {
    T[] values = enumClass.getEnumConstants();
    while (true) {
      System.out.println(prompt + " (введите название или номер):");
      for (int i = 0; i < values.length; i++) {
        System.out.println((i + 1) + " - " + values[i]);
      }
      String line = reader.read();
      if (line == null) throw new IllegalArgumentException("Ошибка: достигнут конец ввода");
      line = line.trim();

      if (line.matches("\\d+")) {
        int index = Integer.parseInt(line) - 1;
        if (index >= 0 && index < values.length) return values[index];
      } else {
        try {
          return Enum.valueOf(enumClass, line.toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }
      }

      System.out.println("Ошибка: выберите одно из предложенных значений.");
    }
  }

  private <T extends Number> Optional<T> readNumber(BufferedReader reader, Parser<T> parser, String errorMessage, boolean mustBePositive) throws IOException {
    return readNumber(reader, parser, errorMessage, mustBePositive, null);
  }

  private <T extends Number> Optional<T> readNumber(BufferedReader reader, Parser<T> parser, String errorMessage, boolean mustBePositive, Double maxValue) throws IOException {
    while (true) {
      String line = reader.readLine();
      if (line == null || line.trim().isEmpty()) return Optional.empty();
      try {
        T value = parser.parse(line.trim());
        if ((!mustBePositive || value.doubleValue() > 0) && (maxValue == null || value.doubleValue() <= maxValue)) {
          return Optional.of(value);
        }
      } catch (NumberFormatException ignored) {}

      if (errorMessage != null) {
        System.out.println("Ошибка: " + errorMessage);
      }
    }
  }

  @FunctionalInterface
  private interface Parser<T> {
    T parse(String input) throws NumberFormatException;
  }

  @FunctionalInterface
  private interface ReaderFunction {
    String read() throws IOException;
  }
}
