package logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerLogger {
  private static final BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();
  private static final String date =
      LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
  private static final String LOG_FILE = "logs/server-" + date + ".log";
  private static volatile boolean loggingEnabled = false;

  private static PrintWriter writer;

  static {
    try {
      File logFile = new File(LOG_FILE);
      File parentDir = logFile.getParentFile();
      if (parentDir != null && !parentDir.exists()) {
        if (!parentDir.mkdirs()) {
          System.err.println("Не удалось создать папку логов: " + parentDir.getAbsolutePath());
        }
      }

      writer = new PrintWriter(new FileWriter(logFile, true));
      Thread writerThread = new Thread(ServerLogger::processLogQueue);
      writerThread.setDaemon(true);
      writerThread.start();
    } catch (IOException e) {
      System.err.println("Ошибка инициализации логгера: " + e.getMessage());
    }
  }

  private static void processLogQueue() {
    try {
      while (true) {
        String entry = logQueue.take();
        if (writer != null) {
          writer.println(entry);
          writer.flush();
        }
      }
    } catch (InterruptedException ignored) {
    }
  }

  public static void log(String message) {
    String timestamp =
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    String entry = "[" + timestamp + "] " + message;

    System.out.println(entry);

    if (loggingEnabled) {
      logQueue.offer(entry);
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
