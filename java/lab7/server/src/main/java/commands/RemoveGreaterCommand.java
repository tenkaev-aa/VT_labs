package commands;

import auth.AuthUtil;
import database.dao.CityDAO;
import database.dao.UserDAO;
import java.util.List;
import java.util.stream.Collectors;
import model.City;
import model.CityComparator;
import network.CommandRequest;
import network.CommandResponse;
import storage.CityManager;

/** Команда для удаления всех элементов, превышающих заданный. */
public class RemoveGreaterCommand implements Command {

  private final CityManager cityManager;
  private final CityDAO cityDAO;
  private final UserDAO userDAO;
  private final CityComparator cityComparator;

  public RemoveGreaterCommand(
      CityManager cityManager, CityDAO cityDAO, UserDAO userDAO, CityComparator cityComparator) {
    this.cityManager = cityManager;
    this.cityDAO = cityDAO;
    this.userDAO = userDAO;
    this.cityComparator = cityComparator;
  }

  @Override
  public CommandResponse execute(CommandRequest request) {
    int userId = AuthUtil.authorizeAndGetUserId(request, userDAO);
    if (userId == -1) {
      return new CommandResponse("Ошибка авторизации: неверный логин или пароль.");
    }

    City baseCity = request.getCity();
    if (baseCity == null) {
      return new CommandResponse("Ошибка: объект города отсутствует в запросе.");
    }

    if (cityManager.getCollection().isEmpty()) {
      return new CommandResponse("Коллекция пуста. Удалять нечего.");
    }

    List<City> toRemove =
        cityManager.getAllCities().stream()
            .filter(c -> c.getOwnerId() == userId)
            .filter(c -> cityComparator.compare(c, baseCity) > 0)
            .toList();

    if (toRemove.isEmpty()) {
      return new CommandResponse("Нет ваших элементов, превышающих заданный.");
    }

    boolean allDeleted =
        cityDAO.deleteByIdsAndOwner(
            toRemove.stream().map(City::getId).collect(Collectors.toList()), userId);

    if (!allDeleted) {
      return new CommandResponse("Ошибка при удалении из базы данных.");
    }

    int removedCount =
        cityManager.removeIf(
            c -> c.getOwnerId() == userId && cityComparator.compare(c, baseCity) > 0);

    return new CommandResponse("Удалено " + removedCount + " элементов.");
  }

  @Override
  public String getDescription() {
    return "удалить элементы, превышающие заданный";
  }

  @Override
  public boolean isAuthorizedOnly() {
    return true;
  }
}
