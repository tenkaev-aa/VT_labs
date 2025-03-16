package data;

import java.io.IOException;
import java.util.Scanner;
import java.util.function.Function;

public class ConsoleDataReader implements DataReader {
    private final Scanner scanner;

    public ConsoleDataReader(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public String readString(String prompt, String errorMessage) throws IOException {
        while (true) {
            System.out.print(prompt); // Выводим приглашение
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("Ошибка: " + errorMessage); // Повторяем ввод
        }
    }

    @Override
    public Long readLong(String prompt, String errorMessage, boolean mustBePositive) throws IOException {
        return readNumber(prompt, errorMessage, mustBePositive, Long::parseLong);
    }

    @Override
    public Integer readInt(String prompt, String errorMessage, boolean mustBePositive) throws IOException {
        return readNumber(prompt, errorMessage, mustBePositive, Integer::parseInt);
    }

    @Override
    public Float readFloat(String prompt, String errorMessage, boolean mustBePositive) throws IOException {
        return readNumber(prompt, errorMessage, mustBePositive, Float::parseFloat);
    }

    @Override
    public Double readDouble(String prompt, String errorMessage, boolean mustBePositive) throws IOException {
        return readNumber(prompt, errorMessage, mustBePositive, Double::parseDouble);
    }

    private <T extends Number> T readNumber(String prompt, String errorMessage, boolean mustBePositive, Function<String, T> parser) {
        while (true) {
            System.out.print(prompt); // Выводим приглашение
            String input = scanner.nextLine().trim();
            try {
                T value = parser.apply(input);
                if (!mustBePositive || value.doubleValue() > 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {}
            System.out.println("Ошибка: " + errorMessage); // Повторяем ввод
        }
    }

    @Override
    public <T extends Enum<T>> T readEnum(String prompt, Class<T> enumClass, String errorMessage) throws IOException {
        T[] values = enumClass.getEnumConstants();
        while (true) {
            System.out.println(prompt + " (введите название или номер):"); // Выводим приглашение
            for (int i = 0; i < values.length; i++) {
                System.out.println((i + 1) + " - " + values[i]);
            }
            String line = scanner.nextLine().trim();
            if (line.matches("\\d+")) {
                int index = Integer.parseInt(line) - 1;
                if (index >= 0 && index < values.length) return values[index];
            } else {
                try {
                    return Enum.valueOf(enumClass, line.toUpperCase());
                } catch (IllegalArgumentException ignored) {}
            }
            System.out.println("Ошибка: " + errorMessage); // Повторяем ввод
        }
    }
}