package network;

import model.City;
import java.io.Serial;
import java.io.Serializable;

public class CommandRequest implements Serializable {
  private final String commandName;
  private final String[] arguments;
  private final City city;
  private final String username;
  private final String password;
  @Serial private static final long serialVersionUID = 5L;

  public CommandRequest(String commandName, String username, String password) {
    this(commandName, new String[0], null, username, password);
  }
  public CommandRequest(String commandName) {
    this(commandName, new String[0], null, null, null);
  }

  public CommandRequest(String commandName, String[] arguments, String username, String password) {
    this(commandName, arguments, null, username, password);
  }

  public CommandRequest(String commandName, City city, String username, String password) {
    this(commandName, new String[0], city, username, password);
  }

  public CommandRequest(String commandName, String[] arguments, City city, String username, String password) {
    this.commandName = commandName;
    this.arguments = arguments;
    this.city = city;
    this.username = username;
    this.password = password;
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
  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }
}
