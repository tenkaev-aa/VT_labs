package commands;

import auth.AuthUtil;
import database.dao.CityDAO;
import database.dao.UserDAO;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import model.City;
import network.CommandRequest;
import network.CommandResponse;
import storage.CityManager;

/** Команда для удаления всех элементов, ключ которых превышает заданный. */
public class RemoveGreaterKeyCommand implements Command {

  private final CityManager cityManager;
  private final CityDAO cityDAO;
  private final UserDAO userDAO;

  public RemoveGreaterKeyCommand(CityManager cityManager, CityDAO cityDAO, UserDAO userDAO) {
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
      return new CommandResponse("Ошибка: необходимо указать ключ.");
    }

    int key;
    try {
      key = Integer.parseInt(args[0]);
    } catch (NumberFormatException e) {
      return new CommandResponse("Ошибка: ключ должен быть целым числом.");
    }

    if (cityManager.getCollection().isEmpty()) {
      return new CommandResponse("Коллекция пуста. Удалять нечего.");
    }

    Map<Integer, City> citiesToRemove =
        cityManager.getCollection().entrySet().stream()
            .filter(e -> e.getKey() > key)
            .filter(e -> e.getValue().getOwnerId() == userId)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    if (citiesToRemove.isEmpty()) {
      return new CommandResponse("Нет ваших элементов с ключами больше " + key + ".");
    }

    List<Integer> ids =
        citiesToRemove.values().stream().map(City::getId).collect(Collectors.toList());

    boolean deleted = cityDAO.deleteByIdsAndOwner(ids, userId);
    if (!deleted) {
      return new CommandResponse("Ошибка при удалении из базы данных.");
    }

    citiesToRemove.keySet().forEach(cityManager::removeCity);

    return new CommandResponse("Удалено " + citiesToRemove.size() + " элементов.");
  }

  @Override
  public String getDescription() {
    return "удалить ваши элементы с ключами больше заданного";
  }

  @Override
  public boolean isAuthorizedOnly() {
    return true;
  }
}
