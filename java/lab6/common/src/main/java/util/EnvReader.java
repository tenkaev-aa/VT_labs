package util;

import java.util.Scanner;

public final class EnvReader {
  private static final String DEFAULT_ENV_NAME = "collection";

  /**
   * Получает путь к файлу из переменной окружения, взаимодействуя с пользователем.
   *
   * @return путь к файлу.
   * @throws IllegalStateException если переменная окружения не установлена и пользователь не ввел
   *     значение.
   */
  public static int getPort(String envName, int defaultPort) {
    Scanner scanner = new Scanner(System.in);
    String portStr = System.getenv(envName);

    if (portStr != null && !portStr.isEmpty()) {
      System.out.println("Обнаружена переменная окружения " + envName + " = " + portStr);
      System.out.print("Хотите использовать её? (y/n): ");
      String choice = scanner.nextLine().trim().toLowerCase();

      if (choice.equals("y") || choice.isEmpty()) {
        try {
          return Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
          System.out.println("Неверный формат порта, используется по умолчанию: " + defaultPort);
        }
      }
    }

    System.out.print("Введите порт вручную (по умолчанию " + defaultPort + "): ");
    String input = scanner.nextLine().trim();

    if (input.isEmpty()) {
      return defaultPort;
    }

    try {
      return Integer.parseInt(input);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Некорректный порт: " + input);
    }
  }

  public static String getFilePath() {
    Scanner scanner = new Scanner(System.in);
    String currentEnv = System.getenv(DEFAULT_ENV_NAME);

    if (currentEnv != null && !currentEnv.isEmpty()) {
      System.out.println(
          "Обнаружена переменная окружения " + DEFAULT_ENV_NAME + " = " + currentEnv);
      System.out.print("Хотите использовать её? (y/n): ");
      String choice = scanner.nextLine().trim().toLowerCase();

      if (choice.equals("y") || choice.equals("yes") || choice.isEmpty()) {
        return currentEnv;
      }
    }

    System.out.print("Введите путь к файлу коллекции: ");
    String userPath = scanner.nextLine().trim();

    if (userPath.isEmpty()) {
      throw new IllegalStateException("Путь к файлу не может быть пустым!");
    }

    return userPath;
  }
}
