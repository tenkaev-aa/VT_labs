package commands;

import model.City;
import model.CityComparator;
import network.CommandRequest;
import network.CommandResponse;
import storage.CityManager;

/** Команда для замены значения по ключу, если новое значение меньше старого. */
public class ReplaceIfLoweCommand implements Command {

  private final CityManager cityManager;
  private final CityComparator cityComparator;

  public ReplaceIfLoweCommand(CityManager cityManager, CityComparator cityComparator) {
    this.cityManager = cityManager;
    this.cityComparator = cityComparator;
  }

  @Override
  public CommandResponse execute(CommandRequest request) {
    String[] args = request.getArguments();
    City newCity = request.getCity();

    if (args.length < 1) {
      return new CommandResponse("Ошибка: id города не указан.");
    }

    if (newCity == null) {
      return new CommandResponse("Ошибка: объект города отсутствует в запросе.");
    }

    int id;
    try {
      id = Integer.parseInt(args[0]);
    } catch (NumberFormatException e) {
      return new CommandResponse("Ошибка: id должен быть целым числом.");
    }

    if (!cityManager.getCollection().containsKey(id)) {
      return new CommandResponse("Город с id " + id + " не найден.");
    }

    City oldCity = cityManager.getCollection().get(id);

    if (cityComparator.compare(newCity, oldCity) < 0) {
      newCity.setId(id);
      cityManager.updateCity(id, newCity);
      return new CommandResponse("Город с id " + id + " успешно заменён.");
    } else {
      return new CommandResponse("Новое значение не меньше старого. Замена не выполнена.");
    }
  }

  @Override
  public String getDescription() {
    return "заменить значение по ключу, если новое значение меньше старого";
  }
}
