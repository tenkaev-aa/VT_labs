package database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {
  private static final Properties props = new Properties();

  static {
    try (InputStream in =
        DatabaseManager.class.getClassLoader().getResourceAsStream("config.properties")) {
      props.load(in);
    } catch (IOException e) {
      throw new RuntimeException("Не удалось загрузить config.properties", e);
    }
  }

  public static Connection getConnection() throws SQLException {
    String url = props.getProperty("db.url");
    String user = props.getProperty("db.user");
    String password = props.getProperty("db.password");

    return DriverManager.getConnection(url, user, password);
  }

  public static String get(String key) {
    return props.getProperty(key);
  }

  public static int getInt(String key, int defaultValue) {
    try {
      return Integer.parseInt(props.getProperty(key));
    } catch (NumberFormatException | NullPointerException e) {
      return defaultValue;
    }
  }
}
