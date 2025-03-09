package commands;

import city.City;
import io.CityInputStrategy;
import io.InputHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;
import storage.CityManager;

public class InsertCommand implements Command, ScriptAwareCommand {
  private final CityManager cityManager;
  private final Scanner scanner;
  private BufferedReader scriptReader;
  private boolean isScriptMode;
  private final CityInputStrategy cityInputStrategy;

  public InsertCommand(CityManager cityManager, Scanner scanner) {
    this.cityManager = cityManager;
    this.scanner = scanner;
    this.cityInputStrategy = new CityInputStrategy();
  }

  @Override
  public void setScriptMode(boolean isScriptMode, BufferedReader scriptReader) {
    this.isScriptMode = isScriptMode;
    this.scriptReader = scriptReader;
  }

  @Override
  public void execute(String[] args) {
    City city = null;
    InputHandler inputHandler = new InputHandler(scanner);
    try {
      if (isScriptMode) {
        city = cityInputStrategy.createFromArgs(scriptReader);
      } else {
        city = inputHandler.inputObject(cityInputStrategy);
      }

      if (city != null) {
        cityManager.addCity(city);
        System.out.println("Элемент успешно добавлен.");
      }
    } catch (IOException e) {
      System.out.println("Ошибка при чтении данных из скрипта: " + e.getMessage());
    } catch (IllegalArgumentException e) {
      System.out.println("Ошибка: " + e.getMessage());
    }
  }

  @Override
  public String getDescription() {
    return "добавить новый элемент";
  }
}
