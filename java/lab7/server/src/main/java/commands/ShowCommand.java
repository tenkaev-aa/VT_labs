package commands;

import auth.AuthUtil;
import database.dao.UserDAO;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import model.City;
import network.CommandRequest;
import network.CommandResponse;
import storage.CityManager;
import storage.CityWithOwnerName;

public class ShowCommand implements Command {

  private final CityManager cityManager;
  private final UserDAO userDAO;

  public ShowCommand(CityManager cityManager, UserDAO userDAO) {
    this.cityManager = cityManager;
    this.userDAO = userDAO;
  }

  @Override
  public CommandResponse execute(CommandRequest request) {
    int userId = AuthUtil.authorizeAndGetUserId(request, userDAO);
    if (userId == -1) {
      return new CommandResponse("Ошибка авторизации: неверный логин или пароль.");
    }

    if (cityManager.getCollectionSize() == 0) {
      return new CommandResponse("Коллекция пуста.");
    }

    List<City> sortedCities =
        cityManager.getAllCities().stream().sorted(Comparator.comparing(City::getName)).toList();

    List<CityWithOwnerName> result =
        sortedCities.stream()
            .map(city -> new CityWithOwnerName(city, userDAO.getUsername(city.getOwnerId())))
            .toList();

    String output =
        result.stream().map(CityWithOwnerName::toString).collect(Collectors.joining("\n"));

    return new CommandResponse("Элементы коллекции (по алфавиту):\n" + output);
  }

  @Override
  public String getDescription() {
    return "вывести все элементы коллекции в алфавитном порядке";
  }

  @Override
  public boolean isAuthorizedOnly() {
    return true;
  }
}
