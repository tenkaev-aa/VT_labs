package commands;

import java.util.stream.Collectors;
import network.CommandRequest;
import network.CommandResponse;

public class HelpCommand implements Command {
  private final CommandManager commandManager;

  public HelpCommand(CommandManager commandManager) {
    this.commandManager = commandManager;
  }

  @Override
  public CommandResponse execute(CommandRequest request) {
    boolean isLoggedIn = request.getUsername() != null && request.getPassword() != null;

    String helpText =
        commandManager.getAll().entrySet().stream()
            .filter(
                e -> {
                  Command cmd = e.getValue();
                  if (cmd.isInternalOnly()) return !isLoggedIn;
                  if (cmd.isAuthorizedOnly()) return isLoggedIn;
                  return true;
                })
            .map(e -> e.getKey() + " : " + e.getValue().getDescription())
            .sorted()
            .collect(Collectors.joining("\n"));

    return new CommandResponse(helpText);
  }

  @Override
  public String getDescription() {
    return "вывести справку по доступным командам";
  }

  @Override
  public boolean isAuthorizedOnly() {
    return false;
  }
}
