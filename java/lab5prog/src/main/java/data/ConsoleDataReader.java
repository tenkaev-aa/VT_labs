package data;

import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Function;

public class ConsoleDataReader implements DataReader {
  private final Scanner scanner;

  public ConsoleDataReader(Scanner scanner) {
    this.scanner = scanner;
  }

  @Override
  public Optional<String> readString(String prompt, String errorMessage, boolean allowNull)
      throws IOException {
    while (true) {
      System.out.print(prompt);
      String input = scanner.nextLine().trim();
      if (allowNull && input.isEmpty()) return Optional.empty();
      if (!input.isEmpty()) return Optional.of(input);
      System.out.println("Ошибка: " + errorMessage);
    }
  }

  @Override
  public Optional<Long> readLong(
      String prompt, String errorMessage, boolean mustBePositive, boolean allowNull)
      throws IOException {
    return readNumber(prompt, errorMessage, mustBePositive, allowNull, Long::parseLong);
  }

  @Override
  public Optional<Integer> readInt(
      String prompt, String errorMessage, boolean mustBePositive, boolean allowNull)
      throws IOException {
    return readNumber(prompt, errorMessage, mustBePositive, allowNull, Integer::parseInt);
  }

  @Override
  public Optional<Float> readFloat(
      String prompt, String errorMessage, boolean mustBePositive, boolean allowNull)
      throws IOException {
    return readNumber(prompt, errorMessage, mustBePositive, allowNull, Float::parseFloat);
  }

  @Override
  public Optional<Double> readDouble(
      String prompt, String errorMessage, boolean mustBePositive, boolean allowNull)
      throws IOException {
    return readNumber(prompt, errorMessage, mustBePositive, allowNull, Double::parseDouble);
  }

  private <T extends Number> Optional<T> readNumber(
      String prompt,
      String errorMessage,
      boolean mustBePositive,
      boolean allowNull,
      Function<String, T> parser) {
    while (true) {
      System.out.print(prompt);
      String input = scanner.nextLine().trim();
      if (allowNull && input.isEmpty()) {
        return Optional.empty();
      }
      try {
        T value = parser.apply(input);
        if (!mustBePositive || value.doubleValue() > 0) {
          return Optional.of(value);
        } else {
          System.out.println("Ошибка: " + errorMessage);
        }
      } catch (NumberFormatException e) {
        System.out.println("Ошибка: " + errorMessage);
      }
    }
  }

  @Override
  public <T extends Enum<T>> Optional<T> readEnum(
      String prompt, Class<T> enumClass, String errorMessage, boolean allowNull)
      throws IOException {
    T[] values = enumClass.getEnumConstants();
    while (true) {
      System.out.println(prompt + " (введите название или номер, или оставьте пустым):");
      for (int i = 0; i < values.length; i++) {
        System.out.println((i + 1) + " - " + values[i]);
      }
      String line = scanner.nextLine().trim();
      if (allowNull && line.isEmpty()) {
        return Optional.empty();
      }
      if (line.matches("\\d+")) {
        int index = Integer.parseInt(line) - 1;
        if (index >= 0 && index < values.length) return Optional.of(values[index]);
      } else {
        try {
          return Optional.of(Enum.valueOf(enumClass, line.toUpperCase()));
        } catch (IllegalArgumentException ignored) {
        }
      }
      System.out.println("Ошибка: " + errorMessage);
    }
  }
}
