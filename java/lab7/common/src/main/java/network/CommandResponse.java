package network;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import model.City;

public class CommandResponse implements Serializable {
  private final String message;
  private final List<City> cities;
  @Serial private static final long serialVersionUID = 10L;

  public CommandResponse(String message) {
    this(message, Collections.emptyList());
  }

  public CommandResponse(String message, List<City> cities) {
    this.message = message;
    this.cities = cities != null ? cities : Collections.emptyList();
  }

  public String getMessage() {
    return message;
  }

  public List<City> getCities() {
    return cities;
  }
}
