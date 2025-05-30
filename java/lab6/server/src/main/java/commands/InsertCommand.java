package commands;

import city.City;
import network.CommandRequest;
import network.CommandResponse;
import storage.CityManager;
import util.IdGenerator;

/** Команда для добавления нового элемента в коллекцию. */
public class InsertCommand implements Command {

  private final CityManager cityManager;

  public InsertCommand(CityManager cityManager) {
    this.cityManager = cityManager;
  }

  @Override
  public CommandResponse execute(CommandRequest request) {
    City city = request.getCity();

    if (city == null) {
      return new CommandResponse("Ошибка: объект города отсутствует в запросе.");
    }

    try {
      int id = IdGenerator.getNextId();
      city.setId(id);
      cityManager.addCity(city);
      return new CommandResponse("Город успешно добавлен.");
    } catch (IllegalArgumentException e) {
      return new CommandResponse("Ошибка при добавлении: " + e.getMessage());
    } catch (Exception e) {
      return new CommandResponse("Неожиданная ошибка: " + e.getMessage());
    }
  }

  @Override
  public String getDescription() {
    return "добавить новый элемент с заданным ключом";
  }
}
