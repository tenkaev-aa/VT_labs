package io;

import city.City;
import exceptions.FileWriteException;
import java.util.List;

public interface CityWriter {
  void writeCitiesToFile(String fileName, List<City> cities) throws FileWriteException;
}
