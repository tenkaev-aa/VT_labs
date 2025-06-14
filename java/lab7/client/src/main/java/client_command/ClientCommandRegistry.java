package client_command;

import data.DataReader;
import input.InputLoopHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import model.City;
import network.CommandSender;

public class ClientCommandRegistry {
  private final Map<String, ClientCommand> commandMap = new HashMap<>();

  public ClientCommandRegistry(
      CommandSender sender,
      DataReader<City> reader,
      InputLoopHandler loop,
      Scanner scanner,
      ClientCommandProcessor processor) {
    register(new InsertCommand(sender, reader));
    register(new UpdateCommand(sender, reader, scanner));
    register(new ReplaceIfLowerCommand(sender, reader));
    register(new RemoveGreaterCommand(sender, reader));
    register(new LoginCommand(sender));
    register(new LogoutCommand());
    register(new RegisterCommand(sender, scanner));
    register(new ExecuteScriptCommand(processor, reader));
    register(new ExitCommand(loop));
    register(new HelpCommand(sender));
  }

  private void register(ClientCommand command) {
    commandMap.put(command.getName(), command);
  }

  public ClientCommand get(String name) {
    return commandMap.get(name.toLowerCase());
  }
}
