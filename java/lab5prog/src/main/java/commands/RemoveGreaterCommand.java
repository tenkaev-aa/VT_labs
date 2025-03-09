package commands;

import city.City;
import city.CityComparator;
import io.CityInputStrategy;
import io.InputHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.function.Predicate;
import storage.CityManager;

/** Команда для удаления элементов, превышающих заданный. */
public class RemoveGreaterCommand implements Command, ScriptAwareCommand {
  private final CityManager cityManager;
  private final Scanner scanner;
  private final CityComparator cityComparator;
  private BufferedReader scriptReader;
  private boolean isScriptMode;

  public RemoveGreaterCommand(
      CityManager cityManager, Scanner scanner, CityComparator cityComparator) {
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
    InputHandler inputHandler = new InputHandler(scanner);
    CityInputStrategy cityInputStrategy = new CityInputStrategy();
    try {
      City city = null;
      if (isScriptMode) {
        city = cityInputStrategy.createFromArgs(scriptReader);
      } else {
        city = inputHandler.inputObject(cityInputStrategy);
      }
      if (cityManager.getCollection().isEmpty()) {
        System.out.println("Коллекция пуста. Удалять нечего.");
        return;
      }

      City finalCity = city;
      Predicate<City> condition = c -> cityComparator.compare(c, finalCity) > 0;
      int removedCount = cityManager.removeIf(condition);

      if (removedCount == 0) {
        System.out.println("Нет элементов, превышающих заданный.");
      } else {
        System.out.println("Удалено " + removedCount + " элементов.");
      }
    } catch (IOException e) {
      System.out.println("Ошибка ввода при обработке скрипта: " + e.getMessage());
    }
  }

  @Override
  public String getDescription() {
    return "удалить элементы, превышающие заданный";
  }
}
