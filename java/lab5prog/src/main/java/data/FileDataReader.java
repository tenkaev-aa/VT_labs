package data;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.function.Function;

public class FileDataReader implements DataReader {
    private final BufferedReader reader;

    public FileDataReader(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public String readString(String prompt, String errorMessage) throws IOException {
        String line = reader.readLine();
        if (line == null || line.trim().isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }
        return line.trim();
    }

    @Override
    public Long readLong(String prompt, String errorMessage, boolean mustBePositive) throws IOException {
        return readNumber(errorMessage, mustBePositive, Long::parseLong);
    }

    @Override
    public Integer readInt(String prompt, String errorMessage, boolean mustBePositive) throws IOException {
        return readNumber(errorMessage, mustBePositive, Integer::parseInt);
    }

    @Override
    public Float readFloat(String prompt, String errorMessage, boolean mustBePositive) throws IOException {
        return readNumber(errorMessage, mustBePositive, Float::parseFloat);
    }

    @Override
    public Double readDouble(String prompt, String errorMessage, boolean mustBePositive) throws IOException {
        return readNumber(errorMessage, mustBePositive, Double::parseDouble);
    }

    private <T extends Number> T readNumber(String errorMessage, boolean mustBePositive, Function<String, T> parser) throws IOException {
        String line = reader.readLine();
        if (line == null || line.trim().isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }
        try {
            T value = parser.apply(line.trim());
            if (!mustBePositive || value.doubleValue() > 0) {
                return value;
            }
        } catch (NumberFormatException ignored) {}
        throw new IllegalArgumentException(errorMessage);
    }

    @Override
    public <T extends Enum<T>> T readEnum(String prompt, Class<T> enumClass, String errorMessage) throws IOException {
        String line = reader.readLine();
        if (line == null) {
            throw new IllegalArgumentException(errorMessage);
        }
        line = line.trim();
        try {
            return Enum.valueOf(enumClass, line.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}