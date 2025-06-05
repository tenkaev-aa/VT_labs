package commands;

import network.CommandRequest;
import network.CommandResponse;
import storage.CityManager;
import util.DateUtils;

/** Команда для вывода информации о коллекции. */
public class InfoCommand implements Command {

  private final CityManager cityManager;

  public InfoCommand(CityManager cityManager) {
    this.cityManager = cityManager;
  }

  @Override
  public CommandResponse execute(CommandRequest request) {
    String type = cityManager.getCollection().getClass().getSimpleName();
    int size = cityManager.getCollectionSize();
    String initDate = DateUtils.formatDateTime(cityManager.getInitializationTime());
    String updateDate = DateUtils.formatDateTime(cityManager.getLastUpdateTime());

    String message =
        String.format(
            "Тип коллекции: %s%n"
                + "Количество элементов: %d%n"
                + "Дата инициализации: %s%n"
                + "Дата последнего изменения: %s",
            type, size, initDate, updateDate);

    return new CommandResponse(message);
  }

  @Override
  public String getDescription() {
    return "вывести информацию о коллекции";
  }
}
