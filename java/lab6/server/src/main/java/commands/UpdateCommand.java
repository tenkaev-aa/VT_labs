package commands;

import city.City;
import network.CommandRequest;
import network.CommandResponse;
import storage.CityManager;

/** Команда для обновления элемента коллекции по id. */
public class UpdateCommand implements Command {

  private final CityManager cityManager;

  public UpdateCommand(CityManager cityManager) {
    this.cityManager = cityManager;
  }

  @Override
  public CommandResponse execute(CommandRequest request) {
    String[] args = request.getArguments();
    City newCity = request.getCity();

    if (args.length < 1) {
      return new CommandResponse("Ошибка: необходимо указать id города.");
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

    newCity.setId(id); // сохранить id
    cityManager.updateCity(id, newCity);
    return new CommandResponse("Город с id " + id + " успешно обновлён.");
  }

  @Override
  public String getDescription() {
    return "обновить значение элемента коллекции по указанному id";
  }
}
