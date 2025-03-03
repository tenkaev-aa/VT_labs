package commands;

import io.XmlWriter;
import storage.CityManager;

/**
 * Команда для сохранения коллекции в файл.
 *
 * <p>Эта команда сохраняет текущее состояние коллекции городов в XML-файл, используя {@link
 * XmlWriter}. Если сохранение проходит успешно, выводится сообщение об успешном завершении
 * операции. В случае ошибки выводится сообщение об ошибке.
 *
 * @see Command
 * @see CityManager
 * @see XmlWriter
 */
public class SaveCommand implements Command {
  private final CityManager cityManager;
  private final String fileName;
  private final XmlWriter xmlWriter;

  /**
   * Создает команду для сохранения коллекции в файл.
   *
   * @param cityManager менеджер коллекции городов, который будет использоваться для получения
   *     данных.
   * @param fileName имя файла, в который будет сохранена коллекция.
   * @param xmlWriter объект для записи данных в XML-файл.
   */
  public SaveCommand(CityManager cityManager, String fileName, XmlWriter xmlWriter) {
    this.cityManager = cityManager;
    this.fileName = fileName;
    this.xmlWriter = xmlWriter;
  }

  /**
   * Выполняет команду, сохраняя коллекцию в файл.
   *
   * <p>Метод использует {@link XmlWriter} для записи данных коллекции в XML-файл. Если операция
   * завершается успешно, выводится сообщение об успешном сохранении. В случае ошибки выводится
   * сообщение с описанием проблемы.
   *
   * @param args аргументы команды (в данной команде не используются).
   */
  @Override
  public void execute(String[] args) {
    try {
      xmlWriter.writeCitiesToFile(fileName, cityManager.getAllCities());
      System.out.println("Коллекция успешно сохранена в файл.");
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
    return "сохранить коллекцию в файл";
  }
}
