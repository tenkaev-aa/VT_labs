package client_command;

import data.DataReader;
import input_object.CityInputStrategy;
import java.io.IOException;
import model.City;
import network.CommandRequest;
import network.CommandResponse;
import network.CommandSender;
import session.CurrentSession;

public class ReplaceIfLowerCommand implements ClientCommand {
  private final CommandSender sender;
  private final DataReader<City> reader;

  public ReplaceIfLowerCommand(CommandSender sender, DataReader<City> reader) {
    this.sender = sender;
    this.reader = reader;
  }

  @Override
  public String getName() {
    return "replace_if_lower";
  }

  @Override
  public void execute(String[] args) throws IOException {
    if (!CurrentSession.isLoggedIn()) {
      System.out.println("[CLIENT] Ошибка: Неизвестная команда: replace_if_lower");
      return;
    }

    if (args.length < 1) {
      System.out.println("[CLIENT] Ошибка: укажите ID города для замены.");
      return;
    }

    int id;
    try {
      id = Integer.parseInt(args[0]);
    } catch (NumberFormatException e) {
      System.out.println("[CLIENT] Ошибка: ID должен быть числом.");
      return;
    }

    CityInputStrategy strategy = new CityInputStrategy(reader);
    City newCity = strategy.inputObject();
    CommandRequest request =
        new CommandRequest(
            getName(),
            new String[] {String.valueOf(id)},
            newCity,
            CurrentSession.getUsername(),
            CurrentSession.getPassword());

    CommandResponse response = sender.send(request);
  }
}
