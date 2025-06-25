package database.util;

import java.io.InputStream;
import java.util.Properties;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
  private static final SessionFactory sessionFactory;

  static {
    try {
      Properties props = new Properties();
      try (InputStream in =
          HibernateUtil.class.getClassLoader().getResourceAsStream("config.properties")) {
        if (in == null) throw new RuntimeException("Файл config.properties не найден");
        props.load(in);
      }

      Configuration configuration = new Configuration();
      configuration.configure("hibernate.cfg.xml");

      configuration.setProperty(
          "hibernate.connection.driver_class", props.getProperty("db.driver"));
      configuration.setProperty("hibernate.connection.url", props.getProperty("db.url"));
      configuration.setProperty("hibernate.connection.username", props.getProperty("db.user"));
      configuration.setProperty("hibernate.connection.password", props.getProperty("db.password"));

      configuration.setProperty("hibernate.dialect", props.getProperty("hibernate.dialect"));
      configuration.setProperty(
          "hibernate.hbm2ddl.auto", props.getProperty("hibernate.hbm2ddl.auto", "none"));
      configuration.setProperty(
          "hibernate.show_sql", props.getProperty("hibernate.show_sql", "false"));

      sessionFactory = configuration.buildSessionFactory();

    } catch (Exception ex) {
      throw new ExceptionInInitializerError("Ошибка инициализации Hibernate: " + ex.getMessage());
    }
  }

  public static SessionFactory getSessionFactory() {
    return sessionFactory;
  }
}
