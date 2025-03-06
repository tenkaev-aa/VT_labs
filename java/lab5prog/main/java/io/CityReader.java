package io;

import city.City;
import exceptions.FileReadException;
import java.util.List;

public interface CityReader {
  List<City> readCities(String fileName) throws FileReadException;
}
