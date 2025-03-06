package io;

import builders.Builder;
import builders.CityBuilder;
import city.City;
import city.Coordinates;
import city.Human;
import enums.Climate;
import enums.Government;
import enums.StandardOfLiving;
import java.util.Optional;
import java.util.Scanner;

public class CityInputStrategy implements InputStrategy<City> {
  @Override
  public City inputObject(Scanner scanner) {
    Builder<City> builder = new CityBuilder();
    System.out.println("Введите данные города:");

    String name = inputString(scanner, "Название: ", "Название не может быть пустым");
    builder.setName(name);

    Coordinates coordinates = inputCoordinates(scanner);
    builder.setNestedObject(coordinates);

    Long area =
        inputNumber(
                scanner,
                "Площадь: ",
                "Площадь должна быть положительным числом",
                Long::parseLong,
                true)
            .orElseThrow(() -> new IllegalArgumentException("Площадь не может быть пустой"));
    builder.setNumber(area);

    Integer population =
        inputNumber(
                scanner,
                "Население: ",
                "Население должно быть положительным числом",
                Integer::parseInt,
                true)
            .orElseThrow(() -> new IllegalArgumentException("Население не может быть пустым"));
    builder.setNumber(population);

    Optional<Float> metersAboveSeaLevel =
        inputNumber(
            scanner,
            "Высота над уровнем моря (можно оставить пустым): ",
            null,
            Float::parseFloat,
            false);
    metersAboveSeaLevel.ifPresent(builder::setNumber);

    Climate climate = inputEnum(scanner, "Выберите климат", Climate.class);
    builder.setEnum(climate);

    Government government = inputEnum(scanner, "Выберите тип правительства", Government.class);
    builder.setEnum(government);

    StandardOfLiving standardOfLiving =
        inputEnum(scanner, "Выберите уровень жизни", StandardOfLiving.class);
    builder.setEnum(standardOfLiving);

    Optional<Human> governor = inputNullableHuman(scanner);
    governor.ifPresent(builder::setNestedObject);

    return builder.build();
  }

  private String inputString(Scanner scanner, String prompt, String errorMessage) {
    String input;
    do {
      System.out.print(prompt);
      input = scanner.nextLine().trim();
      if (!input.isEmpty()) return input;
      System.out.println("Ошибка: " + errorMessage);
    } while (true);
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
    Optional<Float> height =
        inputNumber(
            scanner, "Рост губернатора (можно оставить пустым): ", null, Float::parseFloat, true);
    return height.map(Human::new);
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

  private <T extends Enum<T>> T inputEnum(Scanner scanner, String prompt, Class<T> enumClass) {
    T[] values = enumClass.getEnumConstants();
    while (true) {
      System.out.println(prompt + " (введите название или номер):");
      for (int i = 0; i < values.length; i++) {
        System.out.println((i + 1) + " - " + values[i]);
      }
      String input = scanner.nextLine().trim();
      if (input.matches("\\d+")) {
        int index = Integer.parseInt(input) - 1;
        if (index >= 0 && index < values.length) return values[index];
      } else {
        try {
          return Enum.valueOf(enumClass, input.toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }
      }
      System.out.println("Ошибка: выберите одно из предложенных значений.");
    }
  }

  @FunctionalInterface
  private interface Parser<T> {
    T parse(String input) throws NumberFormatException;
  }
}
