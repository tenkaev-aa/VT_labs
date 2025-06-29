package network;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;
import model.City;

public class CommandRequest implements Serializable {
  private final String commandName;
  private final String[] arguments;
  private final City city;
  private final String username;
  private final String password;
  private final String sessionToken;

  @Serial private static final long serialVersionUID = 5L;

  public CommandRequest(String commandName, String[] arguments, City city, String sessionToken) {
    this.commandName = commandName;
    this.arguments = arguments;
    this.city = city;
    this.sessionToken = sessionToken;
    this.username = null;
    this.password = null;
  }

  public CommandRequest(String commandName, City city, String sessionToken) {
    this(commandName, new String[0], city, sessionToken);
  }

  public CommandRequest(String commandName, String sessionToken) {
    this(commandName, new String[0], (City) null, sessionToken);
  }

  public CommandRequest(String commandName, String[] arguments, String sessionToken) {
    this(commandName, arguments, (City) null, sessionToken);
  }

  public CommandRequest(String commandName, String username, String password) {
    this(commandName, new String[0], null, username, password);
  }

  public CommandRequest(String commandName, String[] arguments, String username, String password) {
    this(commandName, arguments, null, username, password);
  }

  public CommandRequest(String commandName, City city, String username, String password) {
    this(commandName, new String[0], city, username, password);
  }

  public CommandRequest(
      String commandName, String[] arguments, City city, String username, String password) {
    this.commandName = commandName;
    this.arguments = arguments;
    this.city = city;
    this.username = username;
    this.password = password;
    this.sessionToken = null;
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

  public Optional<String> getUsername() {
    return Optional.ofNullable(username);
  }

  public Optional<String> getPassword() {
    return Optional.ofNullable(password);
  }

  public Optional<String> getSessionToken() {
    return Optional.ofNullable(sessionToken);
  }
}
