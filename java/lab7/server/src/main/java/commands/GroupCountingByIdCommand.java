package commands;

import auth.AuthUtil;
import database.dao.UserDAO;
import java.util.Map;
import java.util.stream.Collectors;
import network.CommandRequest;
import network.CommandResponse;
import storage.CityManager;

/**
 * Команда для группировки элементов по четности ID и вывода количества элементов в каждой группе.
 */
public class GroupCountingByIdCommand implements Command {

  private final CityManager cityManager;
  private final UserDAO userDAO;

  public GroupCountingByIdCommand(CityManager cityManager, UserDAO userDAO) {
    this.cityManager = cityManager;
    this.userDAO = userDAO;
  }

  @Override
  public CommandResponse execute(CommandRequest request) {
    int userId = AuthUtil.authorizeAndGetUserId(request, userDAO);
    if (userId == -1) {
      return new CommandResponse("Ошибка авторизации: неверный логин или пароль.");
    }

    if (cityManager.getCollection().isEmpty()) {
      return new CommandResponse("Коллекция пуста. Нечего группировать.");
    }

    Map<Boolean, Long> groupCounts =
        cityManager.getCollection().values().stream()
            .collect(
                Collectors.partitioningBy(city -> city.getId() % 2 == 0, Collectors.counting()));

    long evenCount = groupCounts.getOrDefault(true, 0L);
    long oddCount = groupCounts.getOrDefault(false, 0L);

    String message =
        "Результат группировки по ID:\n"
            + "Четные ID: "
            + evenCount
            + "\n"
            + "Нечетные ID: "
            + oddCount;

    return new CommandResponse(message);
  }

  @Override
  public String getDescription() {
    return "сгруппировать элементы по четности ID и посчитать количество";
  }

  @Override
  public boolean isAuthorizedOnly() {
    return true;
  }
}
