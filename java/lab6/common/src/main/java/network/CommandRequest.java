package network;

import city.City;
import java.io.Serial;
import java.io.Serializable;

public class CommandRequest implements Serializable {
  private final String commandName;
  private final boolean silent;
  private final String[] arguments;
  private final City city;
  @Serial private static final long serialVersionUID = 5L;

  public CommandRequest(String commandName, boolean silent) {
    this(commandName, null, null, silent);
  }

  public CommandRequest(String commandName, String[] arguments, City city) {
    this(commandName, arguments, city, false);
  }

  public CommandRequest(String commandName, String[] arguments, City city, boolean silent) {
    this.commandName = commandName;
    this.arguments = arguments;
    this.city = city;
    this.silent = silent;
  }

  public CommandRequest(String commandName, String[] arguments, boolean silent) {
    this(commandName, arguments, null, silent);
  }

  public CommandRequest(String commandName, City city, boolean silent) {
    this(commandName, new String[0], city, silent);
  }

  public String getCommandName() {
    return commandName;
  }

  public String[] getArguments() {
    return arguments;
  }

  public City getCity() {
    return city;
  }

  public boolean isSilent() {
    return silent;
  }
}
