package data;

import java.io.IOException;
import java.util.Optional;

public interface DataReader {
  Optional<String> readString(String prompt, String errorMessage, boolean allowNull)
      throws IOException;

  Optional<Long> readLong(
      String prompt, String errorMessage, boolean mustBePositive, boolean allowNull)
      throws IOException;

  Optional<Integer> readInt(
      String prompt, String errorMessage, boolean mustBePositive, boolean allowNull)
      throws IOException;

  Optional<Float> readFloat(
      String prompt, String errorMessage, boolean mustBePositive, boolean allowNull)
      throws IOException;

  Optional<Double> readDouble(
      String prompt, String errorMessage, boolean mustBePositive, boolean allowNull)
      throws IOException;

  <T extends Enum<T>> Optional<T> readEnum(
      String prompt, Class<T> enumClass, String errorMessage, boolean allowNull) throws IOException;
}
