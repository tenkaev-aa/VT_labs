package util;

public final class EnvReader {
  private static final String ENV_NAME = "collection"; // Имя переменной окружения

  /**
   * Получает путь к файлу из переменной окружения.
   *
   * @return путь к файлу.
   * @throws IllegalStateException если переменная окружения не установлена.
   */
  public static String getFilePath() {
    String filePath = System.getenv(ENV_NAME);

    if (filePath == null || filePath.isEmpty()) {
      throw new IllegalStateException(
          "Ошибка: переменная окружения " + ENV_NAME + " не установлена!");
    }

    return filePath;
  }
}
