package commands;

import city.City;
import city.CityComparator;
import java.util.function.Predicate;
import network.CommandRequest;
import network.CommandResponse;
import storage.CityManager;

/** Команда для удаления всех элементов, превышающих заданный. */
public class RemoveGreaterCommand implements Command {

  private final CityManager cityManager;
  private final CityComparator cityComparator;

  public RemoveGreaterCommand(CityManager cityManager, CityComparator cityComparator) {
    this.cityManager = cityManager;
    this.cityComparator = cityComparator;
  }

  @Override
  public CommandResponse execute(CommandRequest request) {
    City baseCity = request.getCity();

    if (baseCity == null) {
      return new CommandResponse("Ошибка: объект города отсутствует в запросе.");
    }

    if (cityManager.getCollection().isEmpty()) {
      return new CommandResponse("Коллекция пуста. Удалять нечего.");
    }

    Predicate<City> condition = c -> cityComparator.compare(c, baseCity) > 0;
    int removedCount = cityManager.removeIf(condition);

    if (removedCount == 0) {
      return new CommandResponse("Нет элементов, превышающих заданный.");
    } else {
      return new CommandResponse("Удалено " + removedCount + " элементов.");
    }
  }

  @Override
  public String getDescription() {
    return "удалить элементы, превышающие заданный";
  }
}
