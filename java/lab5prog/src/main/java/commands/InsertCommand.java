package commands;

import city.City;
import data.ConsoleDataReader;
import data.DataReader;
import data.FileDataReader;
import io.CityInputStrategy;

import storage.CityManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;

public class InsertCommand implements Command, ScriptAwareCommand {
  private final CityManager cityManager;
  private final Scanner scanner;
  private BufferedReader scriptReader;
  private boolean isScriptMode;

  public InsertCommand(CityManager cityManager, Scanner scanner) {
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
    try {
      DataReader reader = isScriptMode
              ? new FileDataReader(scriptReader)
              : new ConsoleDataReader(scanner);


      CityInputStrategy cityInputStrategy = new CityInputStrategy(reader);


      City city = cityInputStrategy.inputObject();


      if (city != null) {
        cityManager.addCity(city);
        System.out.println("Элемент успешно добавлен.");
      }
    } catch (IOException e) {
      System.out.println("Ошибка при чтении данных: " + e.getMessage());
    } catch (IllegalArgumentException e) {
      System.out.println("Ошибка: " + e.getMessage());
    }
  }

  @Override
  public String getDescription() {
    return "добавить новый элемент";
  }
}
