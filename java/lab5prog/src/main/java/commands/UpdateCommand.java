package commands;

import city.City;
import io.CityInputStrategy;
import io.InputHandler;
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

      City newCity = null;
      InputHandler inputHandler = new InputHandler(scanner);
      CityInputStrategy cityInputStrategy = new CityInputStrategy();
      if (isScriptMode) {
        newCity = cityInputStrategy.createFromArgs(scriptReader);
      } else {
        newCity = inputHandler.inputObject(cityInputStrategy);
      }

      newCity.setId(id);
      cityManager.updateCity(id, newCity);
      System.out.println("Город с id " + id + " успешно обновлен.");

    } catch (NumberFormatException e) {
      System.out.println("Ошибка: id должен быть целым числом.");
    } catch (IOException e) {
      System.out.println("Ошибка ввода при обработке скрипта: " + e.getMessage());
    } catch (Exception e) {
      System.out.println("Ошибка при обновлении города: " + e.getMessage());
    }
  }

  @Override
  public String getDescription() {
    return "обновить элемент по id";
  }
}
