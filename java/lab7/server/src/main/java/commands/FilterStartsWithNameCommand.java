package commands;

import auth.AuthUtil;
import database.dao.UserDAO;
import java.util.List;
import java.util.stream.Collectors;
import model.City;
import network.CommandRequest;
import network.CommandResponse;
import storage.CityManager;

public class FilterStartsWithNameCommand implements Command {

  private final CityManager cityManager;
  private final UserDAO userDAO;

  public FilterStartsWithNameCommand(CityManager cityManager, UserDAO userDAO) {
    this.cityManager = cityManager;
    this.userDAO = userDAO;
  }

  @Override
  public CommandResponse execute(CommandRequest request) {
    try {
      int userId = AuthUtil.authorizeAndGetUserId(request, userDAO);
      if (userId == -1) {
        return new CommandResponse("Ошибка авторизации: неверный логин или пароль.");
      }

      String[] args = request.getArguments();
      if (args.length < 1) {
        return new CommandResponse("Ошибка: введите подстроку.");
      }

      String prefix = args[0].trim();

      List<City> filtered =
          cityManager.getAllCities().stream()
              .filter(city -> city.getName() != null && city.getName().startsWith(prefix))
              .collect(Collectors.toList());

      if (filtered.isEmpty()) {
        return new CommandResponse("Нет городов, начинающихся с: " + prefix);
      }

      return new CommandResponse("Города, начинающиеся с \"" + prefix + "\":", filtered);
    } catch (Exception e) {
      return new CommandResponse("Неожиданная ошибка: " + e.getMessage());
    }
  }

  @Override
  public String getDescription() {
    return "вывести элементы, название которых начинается с подстроки";
  }

  @Override
  public boolean isAuthorizedOnly() {
    return true;
  }
}
