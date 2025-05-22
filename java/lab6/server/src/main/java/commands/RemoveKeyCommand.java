package commands;

import network.CommandRequest;
import network.CommandResponse;
import storage.CityManager;

/** Команда для удаления элемента по ключу. */
public class RemoveKeyCommand implements Command {

  private final CityManager cityManager;

  public RemoveKeyCommand(CityManager cityManager) {
    this.cityManager = cityManager;
  }

  @Override
  public CommandResponse execute(CommandRequest request) {
    String[] args = request.getArguments();

    if (args.length < 1) {
      return new CommandResponse("Ошибка: необходимо передать id города.");
    }

    try {
      int id = Integer.parseInt(args[0]);

      if (cityManager.getCollection().containsKey(id)) {
        cityManager.removeCity(id);
        return new CommandResponse("Город с id " + id + " успешно удалён.");
      } else {
        return new CommandResponse("Город с id " + id + " не найден.");
      }
    } catch (NumberFormatException e) {
      return new CommandResponse("Ошибка: id должен быть целым числом.");
    }
  }

  @Override
  public String getDescription() {
    return "удалить элемент по ключу";
  }
}
