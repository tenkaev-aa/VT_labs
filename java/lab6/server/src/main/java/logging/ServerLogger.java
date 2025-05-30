package logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServerLogger {
  private static boolean loggingEnabled = false;
  static String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
  private static final String LOG_FILE = "logs/server-" + date + ".log";

  private static PrintWriter writer;

  static {
    try {
      String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
      File logFile = new File("logs/server-" + date + ".log");

      File parentDir = logFile.getParentFile();
      if (parentDir != null && !parentDir.exists()) {
        boolean created = parentDir.mkdirs();
        if (!created) {
          System.err.println("Не удалось создать папку логов: " + parentDir.getAbsolutePath());
        }
      }

      writer = new PrintWriter(new FileWriter(logFile, true));
    } catch (IOException e) {
      System.err.println("Ошибка инициализации логгера: " + e.getMessage());
    }
  }

  public static void log(String message) {
    String timestamp =
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    String entry = "[" + timestamp + "] " + message;

    System.out.println(entry);

    if (loggingEnabled && writer != null) {
      writer.println(entry);
      writer.flush();
    }
  }

  public static void enable() {
    loggingEnabled = true;
    log("Логирование включено.");
  }

  public static void disable() {
    log("Логирование отключено.");
    loggingEnabled = false;
  }

  public static boolean isEnabled() {
    return loggingEnabled;
  }
}
