package commands;

import city.City;
import io.InputHandler;
import java.util.Scanner;
import storage.CityManager;

/**
 * Команда для обновления элемента коллекции по ID.
 *
 * <p>Эта команда позволяет обновить элемент коллекции (город) по указанному ID. Пользователь вводит
 * новые данные для города, которые затем заменяют старые данные. Если элемент с указанным ID не
 * найден, выводится соответствующее сообщение.
 *
 * <p>Для ввода данных используется {@link InputHandler}, который взаимодействует с пользователем
 * через {@link Scanner} и валидирует введенные данные с помощью {@link
 * validation.validationService}.
 *
 * @see Command
 * @see City
 * @see InputHandler
 * @see CityManager
 * @see validation.validationService
 */
public class UpdateCommand implements Command {
  private final CityManager cityManager;
  private final Scanner scanner;

  /**
   * Создает команду для обновления элемента коллекции по ID.
   *
   * @param cityManager менеджер коллекции городов, который будет использоваться для обновления
   *     элемента.
   * @param scanner объект для чтения ввода пользователя.
   */
  public UpdateCommand(CityManager cityManager, Scanner scanner) {
    this.cityManager = cityManager;
    this.scanner = scanner;
  }

  /**
   * Выполняет команду, обновляя элемент коллекции по ID.
   *
   * <p>Метод проверяет, был ли передан ID города в качестве аргумента. Если ID не указан или имеет
   * неверный формат (не является целым числом), выводится сообщение об ошибке. Если элемент с
   * указанным ID не найден, выводится соответствующее сообщение. В противном случае пользователь
   * вводит новые данные для города, которые заменяют старые данные. В случае успешного обновления
   * выводится сообщение об успешном выполнении команды.
   *
   * @param args аргументы команды. Ожидается, что второй аргумент (args[1]) содержит ID города.
   */
  @Override
  public void execute(String[] args) {
    if (args.length == 2) {
      try {
        int id = Integer.parseInt(args[1]);
        if (!cityManager.getCollection().containsKey(id)) {
          System.out.println("Город с id " + id + " не найден.");
          return;
        }
        InputHandler inputHandler = new InputHandler(scanner);
        City newCity = inputHandler.inputCity();
        newCity.setId(id);
        cityManager.updateCity(id, newCity);
        System.out.println("Город с id " + id + " успешно обновлен.");
      } catch (Exception e) {
        System.out.println("Ошибка при обновлении города: " + e.getMessage());
      }
    }
  }

  /**
   * Возвращает описание команды.
   *
   * @return описание команды.
   */
  @Override
  public String getDescription() {
    return "обновить элемент по id";
  }
}
