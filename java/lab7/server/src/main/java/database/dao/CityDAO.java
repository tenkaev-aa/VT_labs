package database.dao;

import java.util.Collection;
import java.util.List;
import model.City;

public interface CityDAO {
  City insert(City city, int ownerId);

  boolean update(int id, City updatedCity, int ownerId);

  boolean delete(int id, int ownerId);

  boolean deleteAllByOwner(int ownerId);

  Collection<City> findAll();

  City findById(int id);

  boolean deleteByIdsAndOwner(List<Integer> ids, int ownerId);
}
