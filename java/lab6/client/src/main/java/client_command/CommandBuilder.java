package client_command;

import city.City;
import data.DataReader;
import input_object.CityInputStrategy;
import java.util.Arrays;
import java.util.Set;
import network.CommandRequest;

public class CommandBuilder {
  private static final Set<String> CITY_COMMANDS =
      Set.of("insert", "update", "replace_if_lowe", "remove_greater");

  public static CommandRequest build(String line, DataReader<City> reader) throws Exception {
    String[] parts = line.trim().split("\\s+");
    if (parts.length == 0 || parts[0].isEmpty()) {
      throw new IllegalArgumentException("Команда не указана.");
    }

    String commandName = parts[0];
    String[] arguments = Arrays.copyOfRange(parts, 1, parts.length);

    City city = null;
    if (CITY_COMMANDS.contains(commandName)) {
      CityInputStrategy strategy = new CityInputStrategy(reader);
      city = strategy.inputObject();
    }

    return new CommandRequest(commandName, arguments, city);
  }
}
