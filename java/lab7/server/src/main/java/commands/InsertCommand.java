package commands;

import auth.AuthUtil;
import database.dao.CityDAO;
import database.dao.UserDAO;
import model.City;
import network.CommandRequest;
import network.CommandResponse;
import storage.CityManager;

/** Команда для добавления нового элемента в коллекцию. */
public class InsertCommand implements Command {

  private final CityManager cityManager;
  private final CityDAO cityDAO;
  private final UserDAO userDAO;

  public InsertCommand(CityManager cityManager, CityDAO cityDAO, UserDAO userDAO) {
    this.cityManager = cityManager;
    this.cityDAO = cityDAO;
    this.userDAO = userDAO;
  }

  @Override
  public CommandResponse execute(CommandRequest request) {
    City city = request.getCity();
    if (city == null) {
      return new CommandResponse("Ошибка: объект города отсутствует в запросе.");
    }

    try {
      int ownerId = AuthUtil.authorizeAndGetUserId(request, userDAO);
      if (ownerId == -1) {
        return new CommandResponse("Ошибка авторизации: неверный логин или пароль.");
      }

      City insertedCity = cityDAO.insert(city, ownerId);
      if (insertedCity == null) {
        return new CommandResponse("Ошибка: не удалось сохранить город в базу данных.");
      }

      cityManager.addCity(insertedCity);

      return new CommandResponse("Город успешно добавлен с ID: " + insertedCity.getId());

    } catch (Exception e) {
      return new CommandResponse("Неожиданная ошибка: " + e.getMessage());
    }
  }

  @Override
  public String getDescription() {
    return "добавить новый элемент в коллекцию";
  }

  @Override
  public boolean isAuthorizedOnly() {
    return true;
  }
}
