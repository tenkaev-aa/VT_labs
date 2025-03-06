package commands;

import city.City;
import city.CityComparator;
import io.CityInputStrategy;
import io.InputHandler;
import java.util.Scanner;
import java.util.function.Predicate;
import storage.CityManager;

/**
 * Команда для удаления элементов, превышающих заданный.
 *
 * <p>Эта команда позволяет пользователю ввести данные о городе, а затем удаляет все элементы
 * коллекции, которые превышают заданный город в соответствии с порядком, определенным {@link
 * CityComparator}.
 *
 * <p>Если коллекция пуста, выводится сообщение о том, что удалять нечего. Если ни один элемент не
 * превышает заданный, выводится соответствующее сообщение. В противном случае выводится количество
 * удаленных элементов.
 *
 * @see Command
 * @see City
 * @see CityComparator
 * @see InputHandler
 * @see CityManager
 * @see validation.validationService
 */
public class RemoveGreaterCommand implements Command {
  private final CityManager cityManager;
  private final Scanner scanner;
  private final CityComparator cityComparator;

  /**
   * Создает команду для удаления элементов, превышающих заданный.
   *
   * @param cityManager менеджер коллекции городов, который будет использоваться для удаления
   *     элементов.
   * @param scanner объект для чтения ввода пользователя.
   * @param cityComparator компаратор для сравнения городов.
   */
  public RemoveGreaterCommand(
      CityManager cityManager, Scanner scanner, CityComparator cityComparator) {
    this.cityManager = cityManager;
    this.scanner = scanner;
    this.cityComparator = cityComparator;
  }

  /**
   * Выполняет команду, удаляя элементы, превышающие заданный.
   *
   * <p>Метод использует {@link InputHandler} для ввода данных о городе. Затем он проверяет, пуста
   * ли коллекция. Если коллекция пуста, выводится сообщение об этом. В противном случае удаляются
   * все элементы, которые превышают заданный город в соответствии с {@link CityComparator}.
   * Выводится количество удаленных элементов.
   *
   * @param args аргументы команды (в данной команде не используются).
   */
  @Override
  public void execute(String[] args) {
    InputHandler inputHandler = new InputHandler(scanner);
    CityInputStrategy cityInputStrategy = new CityInputStrategy();
    City city = inputHandler.inputObject(cityInputStrategy);

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
  }

  /**
   * Возвращает описание команды.
   *
   * @return описание команды.
   */
  @Override
  public String getDescription() {
    return "удалить элементы, превышающие заданный";
  }
}
