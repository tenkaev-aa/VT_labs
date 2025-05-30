package commands;

import java.util.stream.Collectors;
import network.CommandRequest;
import network.CommandResponse;

public class HelpCommand implements Command {
  @Override
  public CommandResponse execute(CommandRequest request) {
    String helpText =
        CommandRegistry.getAll().entrySet().stream()
            .map(e -> e.getKey() + " : " + e.getValue().getDescription())
            .collect(Collectors.joining("\n"));
    return new CommandResponse(helpText);
  }

  @Override
  public String getDescription() {
    return "вывести справку по доступным командам";
  }
}
