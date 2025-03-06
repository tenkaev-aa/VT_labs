package main;

import city.City;
import city.CityComparator;
import commands.CommandHandler;
import exceptions.FileReadException;
import io.CityReader;
import io.XmlReader;
import io.XmlWriter;
import java.util.List;
import java.util.Scanner;
import storage.CityManager;
import util.EnvReader;
import validation.validationService;

/**
 * Главный класс приложения.
 *
 * <p>Этот класс является точкой входа в программу. Он инициализирует необходимые компоненты, такие
 * как {@link CityManager}, {@link CommandHandler}, {@link validationService}, и запускает
 * интерактивный режим работы с пользователем.
 *
 * <p>Программа читает данные о городах из XML-файла, указанного в переменной окружения, и позволяет
 * пользователю выполнять команды для управления коллекцией городов.
 *
 * @see CityManager
 * @see CommandHandler
 * @see validationService
 * @see XmlReader
 * @see XmlWriter
 * @see CityComparator
 */
public class Main {

  /**
   * Точка входа в программу.
   *
   * <p>Метод инициализирует необходимые компоненты, загружает данные о городах из XML-файла и
   * запускает интерактивный режим работы с пользователем. В случае ошибок при чтении файла или
   * других непредвиденных ошибок выводится соответствующее сообщение.
   *
   * @param args аргументы командной строки (не используются).
   */
  public static void main(String[] args) {
    try {
      // Инициализация сервисов и компонентов
      XmlWriter xmlWriter = new XmlWriter();
      CityComparator comparator = new CityComparator();
      CityManager cityManager = new CityManager();

      // Чтение файла из переменной окружения
      String envFilename = EnvReader.getFilePath();
      String filename = "collection.xml"; // Резервный файл, если переменная окружения не задана
      CityReader cityReader = new XmlReader();

      // Загрузка данных о городах из XML-файла
      List<City> cities = cityReader.readCities(envFilename);
      cities.forEach(cityManager::addCity);

      // Инициализация сканера для ввода пользователя
      Scanner scanner = new Scanner(System.in);

      // Создание обработчика команд
      CommandHandler commandHandler =
          new CommandHandler(cityManager, scanner, comparator, xmlWriter);

      // Запуск интерактивного режима
      System.out.println("Программа запущена. Введите команду (help для справки):");

      while (true) {
        if (!scanner.hasNextLine()) {
          System.out.println("\nEOF обнаружен. Введите команду снова.");
          scanner = new Scanner(System.in);
          continue;
        }
        String line = scanner.nextLine().trim();
        commandHandler.handleCommand(line);
      }
    } catch (FileReadException e) {
      System.err.println("Ошибка при чтении файла: " + e.getMessage());
    } catch (Exception e) {
      System.err.println("Произошла непредвиденная ошибка: " + e.getMessage());
    }
  }
}
