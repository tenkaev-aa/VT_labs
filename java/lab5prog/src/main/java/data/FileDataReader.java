package data;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

public class FileDataReader implements DataReader {
  private final BufferedReader reader;

  public FileDataReader(BufferedReader reader) {
    this.reader = reader;
  }

  @Override
  public Optional<String> readString(String prompt, String errorMessage, boolean allowNull)
      throws IOException {
    String line = reader.readLine();
    if (line == null || line.trim().isEmpty()) {
      if (allowNull) return Optional.empty();
      throw new IllegalArgumentException(errorMessage);
    }
    return Optional.of(line.trim());
  }

  @Override
  public Optional<Long> readLong(
      String prompt, String errorMessage, boolean mustBePositive, boolean allowNull)
      throws IOException {
    return readNumber(errorMessage, mustBePositive, allowNull, Long::parseLong);
  }

  @Override
  public Optional<Integer> readInt(
      String prompt, String errorMessage, boolean mustBePositive, boolean allowNull)
      throws IOException {
    return readNumber(errorMessage, mustBePositive, allowNull, Integer::parseInt);
  }

  @Override
  public Optional<Float> readFloat(
      String prompt, String errorMessage, boolean mustBePositive, boolean allowNull)
      throws IOException {
    return readNumber(errorMessage, mustBePositive, allowNull, Float::parseFloat);
  }

  @Override
  public Optional<Double> readDouble(
      String prompt, String errorMessage, boolean mustBePositive, boolean allowNull)
      throws IOException {
    return readNumber(errorMessage, mustBePositive, allowNull, Double::parseDouble);
  }

  private <T extends Number> Optional<T> readNumber(
      String errorMessage, boolean mustBePositive, boolean allowNull, Function<String, T> parser)
      throws IOException {
    String line = reader.readLine();
    if (line == null || line.trim().isEmpty()) {
      if (allowNull) return Optional.empty();
      throw new IllegalArgumentException(errorMessage);
    }
    try {
      T value = parser.apply(line.trim());
      if (!mustBePositive || value.doubleValue() > 0) {
        return Optional.of(value);
      }
    } catch (NumberFormatException ignored) {
    }
    throw new IllegalArgumentException(errorMessage);
  }

  @Override
  public <T extends Enum<T>> Optional<T> readEnum(
      String prompt, Class<T> enumClass, String errorMessage, boolean allowNull)
      throws IOException {
    T[] values = enumClass.getEnumConstants();

    while (true) {

      String line = reader.readLine();
      if (line == null) {
        throw new IOException("Ошибка чтения ввода.");
      }

      line = line.trim();

      if (allowNull && line.isEmpty()) {
        return Optional.empty();
      }

      if (line.matches("\\d+")) {
        int index = Integer.parseInt(line) - 1;
        if (index >= 0 && index < values.length) {
          return Optional.of(values[index]);
        }
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
