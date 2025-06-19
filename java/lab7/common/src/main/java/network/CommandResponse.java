package network;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import model.City;

public class CommandResponse implements Serializable {
  private final String message;
  private final List<City> cities;
  private final String sessionToken;

  @Serial private static final long serialVersionUID = 10L;

  public CommandResponse(String message) {
    this(message, Collections.emptyList(), null);
  }

  public CommandResponse(String message, List<City> cities) {
    this(message, cities, null);
  }

  public CommandResponse(String message, String sessionToken) {
    this(message, Collections.emptyList(), sessionToken);
  }

  public CommandResponse(String message, List<City> cities, String sessionToken) {
    this.message = message;
    this.cities = cities != null ? cities : Collections.emptyList();
    this.sessionToken = sessionToken;
  }

  public String getMessage() {
    return message;
  }

  public List<City> getCities() {
    return cities;
  }

  public Optional<String> getSessionToken() {
    return Optional.ofNullable(sessionToken);
  }
}
