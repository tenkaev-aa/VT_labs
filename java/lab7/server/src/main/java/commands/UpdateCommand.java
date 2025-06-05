package commands;

import database.dao.CityDAO;
import database.dao.UserDAO;
import model.City;
import network.CommandRequest;
import network.CommandResponse;
import storage.CityManager;
/** Команда для обновления элемента коллекции по id. */
public class UpdateCommand implements Command {
  private final CityManager cityManager;
  private final CityDAO cityDAO;
  private final UserDAO userDAO;

  public UpdateCommand(CityManager cityManager, CityDAO cityDAO, UserDAO userDAO) {
    this.cityManager = cityManager;
    this.cityDAO = cityDAO;
    this.userDAO = userDAO;
  }

  @Override
  public CommandResponse execute(CommandRequest request) {
    String[] args = request.getArguments();
    if (args.length < 1) {
      return new CommandResponse("Ошибка: укажите ID объекта для обновления.");
    }

    int id;
    try {
      id = Integer.parseInt(args[0]);
    } catch (NumberFormatException e) {
      return new CommandResponse("Ошибка: ID должен быть числом.");
    }

    City newCity = request.getCity();
    if (newCity == null) {
      return new CommandResponse("Ошибка: объект City не передан.");
    }

    int userId = userDAO.getUserId(request.getUsername());

    boolean success = cityDAO.update(id, newCity, userId);
    if (!success) {
      return new CommandResponse("Ошибка: объект не существует или принадлежит другому пользователю.");
    }

    newCity.setId((int) id);
    cityManager.updateCity(id, newCity);
    return new CommandResponse("Объект успешно обновлён.");
  }

  @Override
  public String getDescription() {
    return "обновить объект по ID (если владелец совпадает)";
  }
}
