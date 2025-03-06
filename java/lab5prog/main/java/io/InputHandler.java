package io;

import city.City;
import city.Coordinates;
import city.Human;
import enums.Climate;
import enums.Government;
import enums.StandardOfLiving;
import java.time.LocalDateTime;
import java.util.Scanner;
import util.DateUtils;
import util.IdGenerator;

/** Класс для обработки ввода пользователя и создания объектов {@link City}. */
public class InputHandler {
  private final Scanner scanner;

  /**
   * Конструктор для инициализации обработчика ввода.
   *
   * @param scanner экземпляр {@link Scanner} для считывания ввода пользователя.
   */
  public InputHandler(Scanner scanner) {
    this.scanner = scanner;
  }

  /**
   * Запрашивает у пользователя ввод данных и создаёт новый объект {@link City}.
   *
   * @return объект {@link City}, созданный на основе пользовательского ввода.
   */
  public City inputCity() {
    System.out.println("Введите данные города:");

    String name = inputString("Название: ", "Название города не может быть пустым");
    Coordinates coordinates = inputCoordinates();
    Long area =
        inputNumber("Площадь: ", "Площадь должна быть положительным числом", Long::parseLong, true);
    Integer population =
        inputNumber(
            "Население: ", "Население должно быть положительным числом", Integer::parseInt, true);
    Float metersAboveSeaLevel =
        inputNumber(
            "Высота над уровнем моря (можно оставить пустым): ", null, Float::parseFloat, false);
    Climate climate = inputEnum("Выберите климат", Climate.class);
    Government government = inputEnum("Выберите тип правительства", Government.class);
    StandardOfLiving standardOfLiving = inputEnum("Выберите уровень жизни", StandardOfLiving.class);
    Human governor = inputNullableHuman();
    LocalDateTime creationDate = DateUtils.getCurrentDateTime();
    Integer id = IdGenerator.getAndIncrement();

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

  /**
   * Запрашивает координаты города.
   *
   * @return объект {@link Coordinates}.
   */
  private Coordinates inputCoordinates() {
    double x =
        inputNumber(
            "Координата X (максимум 36): ",
            "Значение должно быть не больше 36",
            Double::parseDouble,
            true,
            36.0);
    int y = inputNumber("Координата Y: ", "Введите корректное число", Integer::parseInt, true);
    return new Coordinates(x, y);
  }

  /**
   * Запрашивает данные о губернаторе.
   *
   * @return объект {@link Human} или {@code null}, если данные не введены.
   */
  private Human inputNullableHuman() {
    Float height =
        inputNumber("Рост губернатора (можно оставить пустым): ", null, Float::parseFloat, false);
    return (height != null) ? new Human(height) : null;
  }

  /**
   * Запрашивает ввод строки, которая не может быть пустой.
   *
   * @param prompt текст приглашения для ввода.
   * @param errorMessage сообщение об ошибке, если ввод пустой.
   * @return введённая строка.
   */
  private String inputString(String prompt, String errorMessage) {
    String input;
    do {
      System.out.print(prompt);
      input = scanner.nextLine().trim();
      if (!input.isEmpty()) return input;
      System.out.println("Ошибка: " + errorMessage);
    } while (true);
  }

  /**
   * Запрашивает ввод числа.
   *
   * @param prompt текст приглашения для ввода.
   * @param errorMessage сообщение об ошибке, если ввод некорректен.
   * @param parser функциональный интерфейс для преобразования строки в число.
   * @param mustBePositive если {@code true}, значение должно быть положительным.
   * @param <T> тип числа (например, {@link Integer}, {@link Double}).
   * @return введённое число.
   */
  private <T extends Number> T inputNumber(
      String prompt, String errorMessage, Parser<T> parser, boolean mustBePositive) {
    return inputNumber(prompt, errorMessage, parser, mustBePositive, null);
  }

  /**
   * Запрашивает ввод числа с возможностью указания максимального значения.
   *
   * @param prompt текст приглашения для ввода.
   * @param errorMessage сообщение об ошибке, если ввод некорректен.
   * @param parser функциональный интерфейс для преобразования строки в число.
   * @param mustBePositive если {@code true}, значение должно быть положительным.
   * @param maxValue максимальное допустимое значение (может быть {@code null}).
   * @param <T> тип числа.
   * @return введённое число.
   */
  private <T extends Number> T inputNumber(
      String prompt,
      String errorMessage,
      Parser<T> parser,
      boolean mustBePositive,
      Double maxValue) {
    while (true) {
      System.out.print(prompt);
      String input = scanner.nextLine().trim();
      if (input.isEmpty() && errorMessage == null) return null;
      try {
        T value = parser.parse(input);
        if ((!mustBePositive || value.doubleValue() > 0)
            && (maxValue == null || value.doubleValue() <= maxValue)) {
          return value;
        }
      } catch (NumberFormatException ignored) {
      }
      System.out.println(
          "Ошибка: " + (errorMessage != null ? errorMessage : "Введите корректное число."));
    }
  }

  /**
   * Запрашивает ввод значения из перечисления (enum).
   *
   * @param prompt текст приглашения для ввода.
   * @param enumClass класс перечисления.
   * @param <T> тип перечисления.
   * @return выбранное пользователем значение из перечисления.
   */
  private <T extends Enum<T>> T inputEnum(String prompt, Class<T> enumClass) {
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

  /** Функциональный интерфейс для парсинга строк в числовые значения. */
  @FunctionalInterface
  private interface Parser<T> {
    T parse(String input) throws NumberFormatException;
  }
}
