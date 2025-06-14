package commands;

import auth.AuthUtil;
import database.dao.CityDAO;
import database.dao.UserDAO;
import network.CommandRequest;
import network.CommandResponse;
import storage.CityManager;

/**
 * Команда для очистки коллекции.
 *
 * <p>Очищает коллекцию городов на сервере.
 */
public class ClearCommand implements Command {

  private final CityManager cityManager;
  private final CityDAO cityDAO;
  private final UserDAO userDAO;

  public ClearCommand(CityManager cityManager, CityDAO cityDAO, UserDAO userDAO) {
    this.cityManager = cityManager;
    this.cityDAO = cityDAO;
    this.userDAO = userDAO;
  }

  @Override
  public CommandResponse execute(CommandRequest request) {
    try {
      int userId = AuthUtil.authorizeAndGetUserId(request, userDAO);
      if (userId == -1) {
        return new CommandResponse("Ошибка авторизации: неверный логин или пароль.");
      }

      boolean success = cityDAO.deleteAllByOwner(userId);
      if (!success) {
        return new CommandResponse("Ошибка: не удалось очистить коллекцию в базе.");
      }

      cityManager.removeCitiesOwnedBy(userId);

      return new CommandResponse("Коллекция очищена: удалены только ваши объекты.");

    } catch (Exception e) {
      return new CommandResponse("Неожиданная ошибка: " + e.getMessage());
    }
  }

  @Override
  public String getDescription() {
    return "очистить коллекцию";
  }

  @Override
  public boolean isAuthorizedOnly() {
    return true;
  }
}
