package commands;

import city.City;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import network.CommandRequest;
import network.CommandResponse;
import storage.CityManager;

/** Команда для отображения всех элементов коллекции. */
public class ShowCommand implements Command {

  private final CityManager cityManager;

  public ShowCommand(CityManager cityManager) {
    this.cityManager = cityManager;
  }

  @Override
  public CommandResponse execute(CommandRequest request) {
    if (cityManager.getCollectionSize() == 0) {
      return new CommandResponse("Коллекция пуста.");
    }

    List<City> sortedCities =
        cityManager.getAllCities().stream()
            .sorted(Comparator.comparing(City::getName))
            .collect(Collectors.toList());

    return new CommandResponse("Элементы коллекции (по алфавиту):", sortedCities);
  }

  @Override
  public String getDescription() {
    return "вывести все элементы коллекции";
  }
}
