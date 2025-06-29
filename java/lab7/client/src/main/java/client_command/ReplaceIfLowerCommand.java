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
    return "replace_if_lowe";
  }

  @Override
  public void execute(String[] args, DataReader<City> reader) throws IOException {
    if (!CurrentSession.isLoggedIn()) {
      System.out.println("[CLIENT] Ошибка: Неизвестная команда: replace_if_lower");
      return;
    }

    if (args.length < 1) {
      System.out.println("[CLIENT] Ошибка: укажите ID города для замены.");
      return;
    }
    String idArg = args[0];

    CommandRequest checkRequest =
        new CommandRequest(
            "check_ownership",
            new String[] {idArg},
            (City) null,
            CurrentSession.getToken().orElse(null));

    CommandResponse checkResponse = sender.send(checkRequest);

    if (!checkResponse.getMessage().equals("OK")) {
      return;
    }

    CityInputStrategy strategy = new CityInputStrategy(reader);
    City newCity = strategy.inputObject();
    CommandRequest request =
        new CommandRequest(
            getName(), new String[] {args[0]}, newCity, CurrentSession.getToken().orElse(null));

    CommandResponse response = sender.send(request);
  }
}
