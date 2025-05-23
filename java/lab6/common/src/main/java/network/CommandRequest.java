package network;

import city.City;
import java.io.Serial;
import java.io.Serializable;

public class CommandRequest implements Serializable {
  private final String commandName;
  private final String[] arguments;
  private final City city;
  @Serial private static final long serialVersionUID = 5L;

  public CommandRequest(String commandName) {
    this(commandName, null, null);
  }


  public CommandRequest(String commandName, String[] arguments, City city) {
    this.commandName = commandName;
    this.arguments = arguments;
    this.city = city;
  }

  public CommandRequest(String commandName, String[] arguments) {
    this(commandName, arguments, null);
  }

  public CommandRequest(String commandName, City city) {
    this(commandName, new String[0], city);
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
}
