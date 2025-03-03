package commands;

import city.City;
import city.CityComparator;
import io.InputHandler;
import java.util.Scanner;
import storage.CityManager;

/**
 * Команда для замены значения по ключу, если новое значение меньше старого.
 *
 * <p>Эта команда позволяет заменить элемент коллекции по указанному ключу (ID города), если новое
 * значение меньше старого в соответствии с порядком, определенным {@link CityComparator}.
 *
 * <p>Если ключ не указан или имеет неверный формат (не является целым числом), выводится сообщение
 * об ошибке. Если элемент с указанным ключом не найден, выводится соответствующее сообщение. Если
 * новое значение не меньше старого, замена не выполняется, и выводится сообщение об этом.
 *
 * @see Command
 * @see City
 * @see CityComparator
 * @see InputHandler
 * @see CityManager
 * @see validation.validationService
 */
public class ReplaceIfLoweCommand implements Command {
  private final CityManager cityManager;
  private final Scanner scanner;
  private final CityComparator cityComparator;

  /**
   * Создает команду для замены значения по ключу, если новое значение меньше старого.
   *
   * @param cityManager менеджер коллекции городов, который будет использоваться для замены
   *     элемента.
   * @param scanner объект для чтения ввода пользователя.
   * @param cityComparator компаратор для сравнения городов.
   */
  public ReplaceIfLoweCommand(
      CityManager cityManager, Scanner scanner, CityComparator cityComparator) {
    this.cityManager = cityManager;
    this.scanner = scanner;
    this.cityComparator = new CityComparator();
  }

  /**
   * Выполняет команду, заменяя значение по ключу, если новое значение меньше старого.
   *
   * <p>Метод проверяет, был ли передан ключ (ID города) в качестве аргумента. Если ключ не указан
   * или имеет неверный формат (не является целым числом), выводится сообщение об ошибке. Если
   * элемент с указанным ключом не найден, выводится соответствующее сообщение. Если новое значение
   * меньше старого, элемент заменяется, и выводится сообщение об успешной замене. В противном
   * случае выводится сообщение о том, что замена не выполнена.
   *
   * @param args аргументы команды. Ожидается, что второй аргумент (args[1]) содержит ключ (ID
   *     города).
   */
  @Override
  public void execute(String[] args) {
    if (args.length < 2) {
      System.out.println("id города не указано");
      return;
    }

    try {
      int id = Integer.parseInt(args[1]);
      if (!cityManager.getCollection().containsKey(id)) {
        System.out.println("Город с id " + id + " не найден.");
        return;
      }

      InputHandler inputHandler = new InputHandler(scanner);
      City newCity = inputHandler.inputCity();

      City oldCity = cityManager.getCollection().get(id);
      if (cityComparator.compare(newCity, oldCity) < 0) {
        newCity.setId(id);
        cityManager.updateCity(id, newCity);
        System.out.println("Город с id " + id + " успешно заменен.");
      } else {
        System.out.println("Новое значение не меньше старого. Замена не выполнена.");
      }
    } catch (NumberFormatException e) {
      System.out.println("id должен быть целым числом.");
    }
  }

  /**
   * Возвращает описание команды.
   *
   * @return описание команды.
   */
  @Override
  public String getDescription() {
    return "заменить значение, если новое меньше старого";
  }
}
