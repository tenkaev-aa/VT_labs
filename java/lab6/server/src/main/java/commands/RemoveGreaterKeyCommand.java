package commands;

import java.util.List;
import network.CommandRequest;
import network.CommandResponse;
import storage.CityManager;

/** Команда для удаления всех элементов, ключ которых превышает заданный. */
public class RemoveGreaterKeyCommand implements Command {

  private final CityManager cityManager;

  public RemoveGreaterKeyCommand(CityManager cityManager) {
    this.cityManager = cityManager;
  }

  @Override
  public CommandResponse execute(CommandRequest request) {
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

    int initialSize = cityManager.getCollection().size();

    List<Integer> keysToRemove =
        cityManager.getCollection().keySet().stream().filter(k -> k > key).toList();

    keysToRemove.forEach(cityManager::removeCity);

    int removed = initialSize - cityManager.getCollection().size();

    if (removed == 0) {
      return new CommandResponse("Нет элементов с ключами больше " + key + ".");
    } else {
      return new CommandResponse("Удалено " + removed + " элементов.");
    }
  }

  @Override
  public String getDescription() {
    return "удалить элементы с ключами больше заданного";
  }
}
