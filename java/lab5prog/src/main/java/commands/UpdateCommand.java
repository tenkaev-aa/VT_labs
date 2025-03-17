package commands;

import city.City;
import data.ConsoleDataReader;
import data.DataReader;
import data.FileDataReader;
import io.CityInputStrategy;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;
import storage.CityManager;

/** Команда для обновления элемента коллекции по ID. */
public class UpdateCommand implements Command, ScriptAwareCommand {
  private final CityManager cityManager;
  private final Scanner scanner;
  private BufferedReader scriptReader;
  private boolean isScriptMode;

  public UpdateCommand(CityManager cityManager, Scanner scanner) {
    this.cityManager = cityManager;
    this.scanner = scanner;
  }

  @Override
  public void setScriptMode(boolean isScriptMode, BufferedReader scriptReader) {
    this.isScriptMode = isScriptMode;
    this.scriptReader = scriptReader;
  }

  @Override
  public void execute(String[] args) {
    if (args.length < 2) {
      System.out.println("Ошибка: необходимо указать ID города.");
      return;
    }

    try {

      int id = Integer.parseInt(args[1]);

      if (!cityManager.getCollection().containsKey(id)) {
        System.out.println("Город с id " + id + " не найден.");
        return;
      }

      DataReader reader =
          isScriptMode
              ? new FileDataReader(scriptReader) // Для скрипта
              : new ConsoleDataReader(scanner); // Для консоли

      CityInputStrategy cityInputStrategy = new CityInputStrategy(reader);

      City newCity = cityInputStrategy.inputObject();

      newCity.setId(id);

      cityManager.updateCity(id, newCity);
      System.out.println("Город с id " + id + " успешно обновлен.");
    } catch (NumberFormatException e) {
      System.out.println("Ошибка: id должен быть целым числом.");
    } catch (IOException e) {
      System.out.println("Ошибка при чтении данных: " + e.getMessage());
    } catch (IllegalArgumentException e) {
      System.out.println("Ошибка: " + e.getMessage());
    }
  }

  @Override
  public String getDescription() {
    return "обновить элемент по id";
  }
}
