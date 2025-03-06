package commands;

import city.City;
import io.InputHandler;
import java.util.Scanner;
import storage.CityManager;

/**
 * Команда для добавления нового элемента в коллекцию.
 *
 * <p>Эта команда позволяет пользователю добавить новый элемент (город) в коллекцию. Для ввода
 * данных используется {@link InputHandler}, который взаимодействует с пользователем через {@link
 * Scanner} и валидирует введенные данные с помощью {@link validation.validationService}.
 *
 * <p>После успешного ввода данных новый элемент добавляется в коллекцию с помощью {@link
 * CityManager#addCity(City)}.
 *
 * @see Command
 * @see City
 * @see InputHandler
 * @see CityManager
 * @see validation.validationService
 */
public class InsertCommand implements Command {
  private final CityManager cityManager;
  private final Scanner scanner;

  /**
   * Создает команду для добавления нового элемента в коллекцию.
   *
   * @param cityManager менеджер коллекции городов, который будет использоваться для добавления
   *     элемента.
   * @param scanner объект для чтения ввода пользователя.
   */
  public InsertCommand(CityManager cityManager, Scanner scanner) {
    this.cityManager = cityManager;
    this.scanner = scanner;
  }

  /**
   * Выполняет команду, добавляя новый элемент в коллекцию.
   *
   * <p>Метод использует {@link InputHandler} для ввода данных о новом городе. После успешного ввода
   * данных элемент добавляется в коллекцию с помощью {@link CityManager#addCity(City)}. В случае
   * успешного добавления выводится сообщение об успешном выполнении команды.
   *
   * @param args аргументы команды (в данной команде не используются).
   */
  @Override
  public void execute(String[] args) {
    InputHandler inputHandler = new InputHandler(scanner);
    City city = inputHandler.inputCity();
    cityManager.addCity(city);
    System.out.println("Элемент успешно добавлен.");
  }

  /**
   * Возвращает описание команды.
   *
   * @return описание команды.
   */
  @Override
  public String getDescription() {
    return "добавить новый элемент";
  }
}
