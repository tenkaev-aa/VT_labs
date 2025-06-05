package commands;

import model.City;
import java.util.List;
import java.util.stream.Collectors;
import network.CommandRequest;
import network.CommandResponse;
import storage.CityManager;

/** Команда для фильтрации элементов, чьи имена начинаются с заданной подстроки. */
public class FilterStartsWithNameCommand implements Command {

  private final CityManager cityManager;

  public FilterStartsWithNameCommand(CityManager cityManager) {
    this.cityManager = cityManager;
  }

  @Override
  public CommandResponse execute(CommandRequest request) {
    String[] args = request.getArguments();

    if (args.length < 1) {
      return new CommandResponse("Ошибка: введите подстроку.");
    }

    String prefix = args[0].trim();

    List<City> filtered =
        cityManager.getAllCities().stream()
            .filter(city -> city.getName() != null && city.getName().startsWith(prefix))
            .collect(Collectors.toList());

    if (filtered.isEmpty()) {
      return new CommandResponse("Нет городов, начинающихся с: " + prefix);
    }

    return new CommandResponse("Города, начинающиеся с \"" + prefix + "\":", filtered);
  }

  @Override
  public String getDescription() {
    return "вывести элементы, название которых начинается с подстроки";
  }
}
