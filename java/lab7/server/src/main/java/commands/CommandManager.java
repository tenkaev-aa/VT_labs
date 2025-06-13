package commands;

import database.dao.CityDAO;
import database.dao.UserDAO;
import java.util.HashMap;
import java.util.Map;
import model.CityComparator;
import storage.CityManager;

public class CommandManager {
  private final Map<String, Command> commands = new HashMap<>();

  public CommandManager(
      CityManager cityManager, CityDAO cityDAO, UserDAO userDAO, CityComparator cityComparator) {
    commands.put("help", new HelpCommand(this));
    commands.put("clear", new ClearCommand(cityManager, cityDAO, userDAO));
    commands.put("filter_starts_with_name", new FilterStartsWithNameCommand(cityManager, userDAO));
    commands.put("group_counting_by_id", new GroupCountingByIdCommand(cityManager, userDAO));
    commands.put("info", new InfoCommand(cityManager, userDAO));
    commands.put("insert", new InsertCommand(cityManager, cityDAO, userDAO));
    commands.put("print_ascending", new PrintAscendingCommand(cityManager, userDAO));
    commands.put(
        "remove_greater", new RemoveGreaterCommand(cityManager, cityDAO, userDAO, cityComparator));
    commands.put("remove_greater_key", new RemoveGreaterKeyCommand(cityManager, cityDAO, userDAO));
    commands.put("remove_key", new RemoveKeyCommand(cityManager, cityDAO, userDAO));
    commands.put(
        "replace_if_lowe", new ReplaceIfLoweCommand(cityManager, cityDAO, userDAO, cityComparator));
    commands.put("show", new ShowCommand(cityManager, userDAO));
    commands.put("update", new UpdateCommand(cityManager, cityDAO, userDAO));
    commands.put("register", new RegisterCommand(userDAO));
    commands.put("login", new LoginCommand(userDAO));
  }

  public Command get(String name) {
    return commands.get(name);
  }

  public Map<String, Command> getAll() {
    return commands;
  }
}
