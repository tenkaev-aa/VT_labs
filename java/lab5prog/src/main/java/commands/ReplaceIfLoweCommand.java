package commands;

import city.City;
import city.CityComparator;
import data.ConsoleDataReader;
import data.DataReader;
import data.FileDataReader;
import io.CityInputStrategy;
import storage.CityManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;

/** Команда для замены значения по ключу, если новое значение меньше старого. */
public class ReplaceIfLoweCommand implements Command, ScriptAwareCommand {
  private final CityManager cityManager;
  private final Scanner scanner;
  private final CityComparator cityComparator;
  private BufferedReader scriptReader;
  private boolean isScriptMode;

  public ReplaceIfLoweCommand(CityManager cityManager, Scanner scanner, CityComparator cityComparator) {
    this.cityManager = cityManager;
    this.scanner = scanner;
    this.cityComparator = cityComparator;
  }

  @Override
  public void setScriptMode(boolean isScriptMode, BufferedReader scriptReader) {
    this.isScriptMode = isScriptMode;
    this.scriptReader = scriptReader;
  }

  @Override
  public void execute(String[] args) {
    if (args.length < 2) {
      System.out.println("Ошибка: id города не указан.");
      return;
    }

    try {

      int id = Integer.parseInt(args[1]);


      if (!cityManager.getCollection().containsKey(id)) {
        System.out.println("Город с id " + id + " не найден.");
        return;
      }


      DataReader reader = isScriptMode
              ? new FileDataReader(scriptReader)
              : new ConsoleDataReader(scanner);


      CityInputStrategy cityInputStrategy = new CityInputStrategy(reader);


      City newCity = cityInputStrategy.inputObject();


      City oldCity = cityManager.getCollection().get(id);


      if (cityComparator.compare(newCity, oldCity) < 0) {
        newCity.setId(id);
        cityManager.updateCity(id, newCity);
        System.out.println("Город с id " + id + " успешно заменен.");
      } else {
        System.out.println("Новое значение не меньше старого. Замена не выполнена.");
      }
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
    return "заменить значение, если новое меньше старого";
  }
}