package commands;

import auth.AuthUtil;
import database.dao.UserDAO;
import network.CommandRequest;
import network.CommandResponse;
import storage.CityManager;
import util.DateUtils;

/** Команда для вывода информации о коллекции. */
public class InfoCommand implements Command {

  private final CityManager cityManager;
  private final UserDAO userDAO;

  public InfoCommand(CityManager cityManager, UserDAO userDAO) {
    this.cityManager = cityManager;
    this.userDAO = userDAO;
  }

  @Override
  public CommandResponse execute(CommandRequest request) {
    int userId = AuthUtil.authorizeAndGetUserId(request, userDAO);
    if (userId == -1) {
      return new CommandResponse("Ошибка авторизации: неверный логин или пароль.");
    }

    String type = cityManager.getCollection().getClass().getSimpleName();
    int size = cityManager.getCollectionSize();
    String initDate = DateUtils.formatDateTime(cityManager.getInitializationTime());
    String updateDate = DateUtils.formatDateTime(cityManager.getLastUpdateTime());

    String message =
        String.format(
            "Тип коллекции: %s%n"
                + "Количество элементов: %d%n"
                + "Дата инициализации: %s%n"
                + "Дата последнего изменения: %s",
            type, size, initDate, updateDate);

    return new CommandResponse(message);
  }

  @Override
  public String getDescription() {
    return "вывести информацию о коллекции";
  }

  @Override
  public boolean isAuthorizedOnly() {
    return true;
  }
}
