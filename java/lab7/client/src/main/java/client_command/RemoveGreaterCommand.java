package client_command;

import data.DataReader;
import input_object.CityInputStrategy;
import model.City;
import network.CommandRequest;
import network.CommandResponse;
import network.CommandSender;
import session.CurrentSession;

public class RemoveGreaterCommand implements ClientCommand {
  private final CommandSender sender;
  private final DataReader<City> reader;

  public RemoveGreaterCommand(CommandSender sender, DataReader<City> reader) {
    this.sender = sender;
    this.reader = reader;
  }

  @Override
  public String getName() {
    return "remove_greater";
  }

  @Override
  public void execute(String[] args, DataReader<City> reader) {
    if (!CurrentSession.isLoggedIn()) {
      System.out.println("[CLIENT] Ошибка: Неизвестная команда: remove_greater ");
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
    try {
      CityInputStrategy strategy = new CityInputStrategy(reader);
      City city = strategy.inputObject();
      CommandRequest request =
          new CommandRequest(
              getName(),
              args,
              city,
              CurrentSession.getUsername().orElse(null),
              CurrentSession.getPassword().orElse(null));

      CommandResponse response = sender.send(request);

    } catch (Exception e) {
      System.out.println("[CLIENT] Ошибка при вводе города: " + e.getMessage());
    }
  }
}
