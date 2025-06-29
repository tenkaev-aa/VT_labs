package commands;

import auth.AuthUtil;
import database.dao.UserDAO;
import model.City;
import network.CommandRequest;
import network.CommandResponse;
import storage.CityManager;

public class CheckOwnershipCommand implements Command {
  private final CityManager cityManager;
  private final UserDAO userDAO;

  public CheckOwnershipCommand(CityManager cityManager, UserDAO userDAO) {
    this.cityManager = cityManager;
    this.userDAO = userDAO;
  }

  @Override
  public CommandResponse execute(CommandRequest request) {
    int userId = AuthUtil.authorizeAndGetUserId(request, userDAO);
    if (userId == -1) {
      return new CommandResponse("Ошибка авторизации.");
    }

    String[] args = request.getArguments();
    if (args.length < 1) return new CommandResponse("Не передан ID");

    try {
      int id = Integer.parseInt(args[0]);
      City city = cityManager.getCity(id);
      if (city == null) return new CommandResponse("Город с ID " + id + " не найден.");
      if (city.getOwnerId() != userId) return new CommandResponse("Вы не владелец этого города.");
      return new CommandResponse("OK");
    } catch (NumberFormatException e) {
      return new CommandResponse("ID должен быть числом.");
    }
  }

  @Override
  public String getDescription() {
    return "проверка, принадлежит ли объект вам ";
  }

  @Override
  public boolean isHidden() {
    return true;
  }
}
