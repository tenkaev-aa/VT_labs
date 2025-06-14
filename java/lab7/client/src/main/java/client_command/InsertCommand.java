package client_command;

import data.DataReader;
import input_object.CityInputStrategy;
import java.io.IOException;
import model.City;
import network.CommandRequest;
import network.CommandResponse;
import network.CommandSender;
import session.CurrentSession;

public class InsertCommand implements ClientCommand {
  private final CommandSender sender;
  private final DataReader<City> reader;

  public InsertCommand(CommandSender sender, DataReader<City> reader) {
    this.sender = sender;
    this.reader = reader;
  }

  @Override
  public void execute(String[] args, DataReader<City> reader) throws IOException {
    if (!CurrentSession.isLoggedIn()) {
      System.out.println("[CLIENT] Неизвестная команда: insert");
      return;
    }
    CityInputStrategy strategy = new CityInputStrategy(reader);
    City city = strategy.inputObject();

    CommandRequest request =
        new CommandRequest(
            "insert", args, city, CurrentSession.getUsername(), CurrentSession.getPassword());

    CommandResponse response = sender.send(request);
  }

  @Override
  public String getName() {
    return "insert";
  }
}
