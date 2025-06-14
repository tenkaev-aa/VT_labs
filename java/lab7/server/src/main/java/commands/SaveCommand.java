package commands;

import data.CityData;
import data.CityDataConverter;
import data.DataHandlerFactory;
import data.DataWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;
import model.City;
import storage.CityManager;

/**
 * Команда для локального сохранения коллекции на сервере. Не регистрируется в CommandRegistry и не
 * доступна клиенту.
 */
public class SaveCommand {

  private final CityManager cityManager;
  private final String fileName;

  public SaveCommand(CityManager cityManager, String fileName) {
    this.cityManager = cityManager;
    this.fileName = fileName;
  }

  public void execute() {
    try {
      List<City> cities = cityManager.getAllCities();
      List<CityData> cityDataList = CityDataConverter.toCityDataList(cities);

      if (!checkFileWritable(fileName)) {
        System.err.println("Ошибка: невозможно записать в файл " + fileName);
        return;
      }

      DataWriter writer = DataHandlerFactory.getWriter(fileName);
      writer.writeDataToFile(fileName, cityDataList);
      System.out.println("Коллекция успешно сохранена в файл: " + fileName);
    } catch (Exception e) {
      System.err.println("Ошибка при сохранении коллекции: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private boolean checkFileWritable(String fileName) {
    File file = new File(fileName);
    if (file.exists()) {
      return file.canWrite();
    } else {
      try {
        return file.createNewFile() && file.delete();
      } catch (IOException e) {
        return false;
      }
    }
  }
}
