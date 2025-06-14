package commands;

import auth.AuthUtil;
import database.dao.CityDAO;
import database.dao.UserDAO;
import network.CommandRequest;
import network.CommandResponse;
import storage.CityManager;

/** Команда для удаления элемента по ключу. */
public class RemoveKeyCommand implements Command {
  private final CityManager cityManager;
  private final CityDAO cityDAO;
  private final UserDAO userDAO;

  public RemoveKeyCommand(CityManager cityManager, CityDAO cityDAO, UserDAO userDAO) {
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
      return new CommandResponse("Ошибка: укажите ключ (id) для удаления.");
    }

    int id;
    try {
      id = Integer.parseInt(args[0]);
    } catch (NumberFormatException e) {
      return new CommandResponse("Ошибка: ключ должен быть целым числом.");
    }

    boolean success = cityDAO.delete(id, userId);
    if (!success) {
      return new CommandResponse("Ошибка: объект не найден или не принадлежит вам.");
    }

    cityManager.removeCity(id);
    return new CommandResponse("Объект с ключом " + id + " удалён.");
  }

  @Override
  public String getDescription() {
    return "удалить ваш элемент по ключу (если он вам принадлежит)";
  }

  @Override
  public boolean isAuthorizedOnly() {
    return true;
  }
}
