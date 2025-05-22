package commands;

import network.CommandRequest;
import network.CommandResponse;
import storage.CityManager;

/**
 * Команда для очистки коллекции.
 *
 * <p>Очищает коллекцию городов на сервере.
 */
public class ClearCommand implements Command {

  private final CityManager cityManager;

  public ClearCommand(CityManager cityManager) {
    this.cityManager = cityManager;
  }

  @Override
  public CommandResponse execute(CommandRequest request) {
    cityManager.clearCollection();
    return new CommandResponse("Коллекция успешно очищена.");
  }

  @Override
  public String getDescription() {
    return "очистить коллекцию";
  }
}
