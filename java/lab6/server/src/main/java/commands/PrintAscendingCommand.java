package commands;

import city.City;
import city.CityComparator;
import java.util.List;
import java.util.stream.Collectors;
import network.CommandRequest;
import network.CommandResponse;
import storage.CityManager;

/** Команда для вывода элементов коллекции в порядке возрастания. */
public class PrintAscendingCommand implements Command {

  private final CityManager cityManager;

  public PrintAscendingCommand(CityManager cityManager) {
    this.cityManager = cityManager;
  }

  @Override
  public CommandResponse execute(CommandRequest request) {
    List<City> allCities = cityManager.getAllCities();

    if (allCities.isEmpty()) {
      return new CommandResponse("Коллекция пуста.");
    }

    List<City> sorted =
        allCities.stream().sorted(new CityComparator()).collect(Collectors.toList());

    return new CommandResponse("Города в порядке возрастания:", sorted);
  }

  @Override
  public String getDescription() {
    return "вывести элементы в порядке возрастания";
  }
}
