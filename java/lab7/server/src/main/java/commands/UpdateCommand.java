package commands;

import auth.AuthUtil;
import database.dao.CityDAO;
import database.dao.UserDAO;
import model.City;
import network.CommandRequest;
import network.CommandResponse;
import storage.CityManager;

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
    int userId = AuthUtil.authorizeAndGetUserId(request, userDAO);
    if (userId == -1) {
      return new CommandResponse("Ошибка авторизации: неверный логин или пароль.");
    }

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
      return new CommandResponse("Ошибка: объект города не передан.");
    }

    City existing = cityManager.getCity(id);
    if (existing == null) {
      return new CommandResponse("Ошибка: объект с ID " + id + " не найден.");
    }

    if (existing.getOwnerId() != userId) {
      return new CommandResponse("Ошибка: вы не являетесь владельцем этого объекта.");
    }

    newCity.setId(id);
    newCity.setOwnerId(userId);

    boolean updated = cityDAO.update(id, newCity, userId);
    if (!updated) {
      return new CommandResponse("Ошибка при обновлении объекта в базе данных.");
    }

    cityManager.updateCity(id, newCity);
    return new CommandResponse("Объект с ID " + id + " успешно обновлён.");
  }

  @Override
  public String getDescription() {
    return "обновить ваш объект по ID";
  }

  @Override
  public boolean isAuthorizedOnly() {
    return true;
  }
}
