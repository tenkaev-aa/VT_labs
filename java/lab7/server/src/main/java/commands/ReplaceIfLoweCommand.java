package commands;

import auth.AuthUtil;
import database.dao.CityDAO;
import database.dao.UserDAO;
import model.City;
import model.CityComparator;
import network.CommandRequest;
import network.CommandResponse;
import storage.CityManager;

/** Команда для замены значения по ключу, если новое значение меньше старого. */
public class ReplaceIfLoweCommand implements Command {

  private final CityManager cityManager;
  private final CityDAO cityDAO;
  private final UserDAO userDAO;
  private final CityComparator cityComparator;

  public ReplaceIfLoweCommand(
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

    String[] args = request.getArguments();
    City newCity = request.getCity();

    if (args.length < 1) {
      return new CommandResponse("Ошибка: id города не указан.");
    }

    if (newCity == null) {
      return new CommandResponse("Ошибка: объект города отсутствует в запросе.");
    }

    int id;
    try {
      id = Integer.parseInt(args[0]);
    } catch (NumberFormatException e) {
      return new CommandResponse("Ошибка: id должен быть целым числом.");
    }

    City oldCity = cityManager.getCollection().get(id);
    if (oldCity == null) {
      return new CommandResponse("Город с id " + id + " не найден.");
    }

    if (oldCity.getOwnerId() != userId) {
      return new CommandResponse("Ошибка: вы не владелец этого города.");
    }

    if (cityComparator.compare(newCity, oldCity) >= 0) {
      return new CommandResponse("Новое значение не меньше старого. Замена не выполнена.");
    }

    newCity.setId(id);
    newCity.setOwnerId(userId);

    boolean updated = cityDAO.update(id, newCity, userId);
    if (!updated) {
      return new CommandResponse("Ошибка при обновлении города в базе данных.");
    }

    cityManager.updateCity(id, newCity);
    return new CommandResponse("Город с id " + id + " успешно заменён.");
  }

  @Override
  public String getDescription() {
    return "заменить элемент по ключу, если новое значение меньше старого";
  }

  @Override
  public boolean isAuthorizedOnly() {
    return true;
  }
}
