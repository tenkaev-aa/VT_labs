package commands;

import auth.AuthUtil;
import database.dao.UserDAO;
import java.util.List;
import java.util.stream.Collectors;
import model.City;
import model.CityComparator;
import network.CommandRequest;
import network.CommandResponse;
import storage.CityManager;

/** Команда для вывода элементов коллекции в порядке возрастания. */
public class PrintAscendingCommand implements Command {

  private final CityManager cityManager;
  private final UserDAO userDAO;

  public PrintAscendingCommand(CityManager cityManager, UserDAO userDAO) {
    this.cityManager = cityManager;
    this.userDAO = userDAO;
  }

  @Override
  public CommandResponse execute(CommandRequest request) {
    int userId = AuthUtil.authorizeAndGetUserId(request, userDAO);
    if (userId == -1) {
      return new CommandResponse("Ошибка авторизации: неверный логин или пароль.");
    }

    List<City> allCities = cityManager.getAllCities();

    if (allCities.isEmpty()) {
      return new CommandResponse("Коллекция пуста.");
    }

    List<City> sorted =
        allCities.stream().sorted(new CityComparator()).collect(Collectors.toList());

    return new CommandResponse("Города в порядке возрастания:", sorted);
  }

  @Override
  public String getDescription() {
    return "вывести элементы коллекции в порядке возрастания";
  }

  @Override
  public boolean isAuthorizedOnly() {
    return true;
  }
}
