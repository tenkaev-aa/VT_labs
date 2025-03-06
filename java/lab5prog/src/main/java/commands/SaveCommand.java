package commands;

import city.City;
import data.CityData;
import data.CityDataConverter;
import data.DataHandlerFactory;
import data.DataWriter;
import java.util.List;
import storage.CityManager;

/**
 * Команда для сохранения коллекции в файл.
 *
 * <p>Эта команда сохраняет текущее состояние коллекции городов в файл (XML или JSON), используя
 * соответствующий {@link DataWriter}. Если сохранение проходит успешно, выводится сообщение об
 * успешном завершении операции. В случае ошибки выводится сообщение об ошибке.
 *
 * @see Command
 * @see CityManager
 * @see DataWriter
 */
public class SaveCommand implements Command {
  private final CityManager cityManager;
  private final String defaultFileName;

  /**
   * Создает команду для сохранения коллекции в файл.
   *
   * @param cityManager менеджер коллекции городов, который будет использоваться для получения
   *     данных.
   * @param defaultFileName имя файла по умолчанию, в который будет сохранена коллекция.
   */
  public SaveCommand(CityManager cityManager, String defaultFileName) {
    this.cityManager = cityManager;
    this.defaultFileName = defaultFileName;
  }

  /**
   * Выполняет команду, сохраняя коллекцию в файл.
   *
   * <p>Метод использует {@link DataHandlerFactory} для создания соответствующего {@link DataWriter}
   * (XML или JSON) и записывает данные коллекции в файл. Если операция завершается успешно,
   * выводится сообщение об успешном сохранении. В случае ошибки выводится сообщение с описанием
   * проблемы.
   *
   * @param args аргументы команды. Ожидается, что первый аргумент (args[1]) указывает формат
   *     файла ("xml" или "json").
   */
  @Override
  public void execute(String[] args) {
    if (args.length < 2) {
      System.out.println("Ошибка: укажите формат файла (xml или json).");
      return;
    }

    String format = args[1].toLowerCase();
    if (!format.equals("xml") && !format.equals("json")) {
      System.out.println("Ошибка: неподдерживаемый формат файла. Используйте 'xml' или 'json'.");
      return;
    }

    try {
      List<City> cities = cityManager.getAllCities();
      List<CityData> cityDataList = CityDataConverter.toCityDataList(cities);

      // Определяем имя файла на основе формата
      String fileName = defaultFileName.replace(".xml", "." + format);

      // Получаем соответствующий DataWriter на основе формата
      DataWriter writer = DataHandlerFactory.getWriter(fileName);

      // Сохраняем данные в файл
      writer.writeDataToFile(fileName, cityDataList);
      System.out.println("Коллекция успешно сохранена в файл: " + fileName);
    } catch (Exception e) {
      System.out.println("Ошибка при сохранении коллекции: " + e.getMessage());
    }
  }

  /**
   * Возвращает описание команды.
   *
   * @return описание команды.
   */
  @Override
  public String getDescription() {
    return "сохранить коллекцию в файл (xml или json)";
  }
}