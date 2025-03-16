package commands;

import city.City;
import city.CityComparator;
import io.CityInputStrategy;
import data.FileDataReader;
import data.DataReader;
import data.ConsoleDataReader;
import storage.CityManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.function.Predicate;

/** Команда для удаления элементов, превышающих заданный. */
public class RemoveGreaterCommand implements Command, ScriptAwareCommand {
  private final CityManager cityManager;
  private final Scanner scanner;
  private final CityComparator cityComparator;
  private BufferedReader scriptReader;
  private boolean isScriptMode;

  public RemoveGreaterCommand(CityManager cityManager, Scanner scanner, CityComparator cityComparator) {
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
    try {
      DataReader reader = isScriptMode
              ? new FileDataReader(scriptReader)
              : new ConsoleDataReader(scanner);

      CityInputStrategy cityInputStrategy = new CityInputStrategy(reader);


      City city = cityInputStrategy.inputObject();


      if (cityManager.getCollection().isEmpty()) {
        System.out.println("Коллекция пуста. Удалять нечего.");
        return;
      }

      Predicate<City> condition = c -> cityComparator.compare(c, city) > 0;
      int removedCount = cityManager.removeIf(condition);


      if (removedCount == 0) {
        System.out.println("Нет элементов, превышающих заданный.");
      } else {
        System.out.println("Удалено " + removedCount + " элементов.");
      }
    } catch (IOException e) {
      System.out.println("Ошибка при чтении данных: " + e.getMessage());
    } catch (IllegalArgumentException e) {
      System.out.println("Ошибка: " + e.getMessage());
    }
  }

  @Override
  public String getDescription() {
    return "удалить элементы, превышающие заданный";
  }
}