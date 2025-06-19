package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HashingConfig {
  private static final String CONFIG_FILE = "hasherconfig.properties";
  private static final int DEFAULT_SALT_LENGTH = 8;
  private static final int DEFAULT_ITERATIONS = 1488;

  private static final Properties props = new Properties();

  static {
    try (InputStream in = HashingConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
      if (in != null) {
        props.load(in);
      } else {
        System.err.println(
            "[CONFIG] Не найден файл: " + CONFIG_FILE + ", используются значения по умолчанию.");
      }
    } catch (IOException e) {
      System.err.println("[CONFIG] Ошибка чтения " + CONFIG_FILE + ": " + e.getMessage());
    }
  }

  public static int getSaltLength() {
    try {
      return Integer.parseInt(
          props.getProperty("salt.length", String.valueOf(DEFAULT_SALT_LENGTH)));
    } catch (NumberFormatException e) {
      return DEFAULT_SALT_LENGTH;
    }
  }

  public static int getIterations() {
    try {
      return Integer.parseInt(
          props.getProperty("hash.iterations", String.valueOf(DEFAULT_ITERATIONS)));
    } catch (NumberFormatException e) {
      return DEFAULT_ITERATIONS;
    }
  }
}
