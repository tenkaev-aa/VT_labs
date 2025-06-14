package client_command;

import data.DataReader;
import java.io.IOException;
import model.City;

public interface ClientCommand {
  void execute(String[] args, DataReader<City> reader) throws IOException;

  String getName();
}
