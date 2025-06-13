package client_command;

import data.DataReader;
import model.City;
import network.CommandRequest;
import network.CommandResponse;
import network.CommandSender;
import session.CurrentSession;

public class HelpCommand implements ClientCommand {

  private final CommandSender sender;

  public HelpCommand(CommandSender sender) {
    this.sender = sender;
  }

  @Override
  public void execute(String[] args, DataReader<City> reader) {
    CommandRequest request =
        new CommandRequest(
            "help", args, null, CurrentSession.getUsername(), CurrentSession.getPassword());

    CommandResponse response = sender.send(request);
  }

  @Override
  public String getName() {
    return "help";
  }
}
