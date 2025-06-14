package database.daoimpl;

import database.DatabaseManager;
import database.dao.CityDAO;
import enums.Climate;
import enums.Government;
import enums.StandardOfLiving;
import java.sql.*;
import java.util.*;
import model.City;
import model.Coordinates;
import model.Human;

public class PostgreCityDAO implements CityDAO {

  @Override
  public City insert(City city, int ownerId) {
    String sql =
        """
        INSERT INTO cities (
            name, coordinates_x, coordinates_y, creation_date, area, population,
            meters_above_sea_level, climate, government, standard_of_living,
            governor_height, owner_id
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        RETURNING id
        """;

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, city.getName());
      stmt.setDouble(2, city.getCoordinates().getX());
      stmt.setInt(3, city.getCoordinates().getY());
      stmt.setTimestamp(4, Timestamp.valueOf(city.getCreationDate()));
      stmt.setDouble(5, city.getArea());
      stmt.setInt(6, city.getPopulation());
      if (city.getMetersAboveSeaLevel() != null) {
        stmt.setFloat(7, city.getMetersAboveSeaLevel());
      } else {
        stmt.setNull(7, Types.REAL);
      }

      stmt.setObject(8, city.getClimate().name(), Types.OTHER);
      stmt.setObject(9, city.getGovernment().name(), Types.OTHER);
      stmt.setObject(10, city.getStandardOfLiving().name(), Types.OTHER);

      if (city.getGovernor() != null) {
        stmt.setFloat(11, city.getGovernor().getHeight());
      } else {
        stmt.setNull(11, Types.REAL);
      }

      stmt.setInt(12, ownerId);
      city.setOwnerId(ownerId);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        city.setId((int) rs.getInt("id"));
        return city;
      }

    } catch (SQLException e) {
      System.err.println("[insert ERROR] " + e.getMessage());
    }

    return null;
  }

  @Override
  public boolean update(int id, City updatedCity, int ownerId) {
    String checkOwner = "SELECT owner_id FROM cities WHERE id = ?";
    String updateSql =
        """
        UPDATE cities SET
          name=?, coordinates_x=?, coordinates_y=?, creation_date=?,
          area=?, population=?, meters_above_sea_level=?,
          climate=?, government=?, standard_of_living=?, governor_height=?
        WHERE id=? AND owner_id=?
    """;

    try (Connection conn = DatabaseManager.getConnection()) {
      // Проверка владельца
      try (PreparedStatement check = conn.prepareStatement(checkOwner)) {
        check.setInt(1, id);
        ResultSet rs = check.executeQuery();
        if (!rs.next() || rs.getInt("owner_id") != ownerId) return false;
      }

      // Обновление
      try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
        stmt.setString(1, updatedCity.getName());
        stmt.setDouble(2, updatedCity.getCoordinates().getX());
        stmt.setInt(3, updatedCity.getCoordinates().getY());
        stmt.setTimestamp(4, Timestamp.valueOf(updatedCity.getCreationDate()));
        stmt.setDouble(5, updatedCity.getArea());
        stmt.setInt(6, updatedCity.getPopulation());
        if (updatedCity.getMetersAboveSeaLevel() != null)
          stmt.setFloat(7, updatedCity.getMetersAboveSeaLevel());
        else stmt.setNull(7, Types.REAL);

        stmt.setObject(8, updatedCity.getClimate().name(), Types.OTHER);
        stmt.setObject(9, updatedCity.getGovernment().name(), Types.OTHER);
        stmt.setObject(10, updatedCity.getStandardOfLiving().name(), Types.OTHER);

        if (updatedCity.getGovernor() != null)
          stmt.setFloat(11, updatedCity.getGovernor().getHeight());
        else stmt.setNull(11, Types.REAL);

        stmt.setInt(12, id);
        stmt.setInt(13, ownerId);

        return stmt.executeUpdate() > 0;
      }

    } catch (SQLException e) {
      System.err.println("[delete ERROR] " + e.getMessage());
      return false;
    }
  }

  @Override
  public boolean delete(int id, int ownerId) {
    String sql = "DELETE FROM cities WHERE id = ? AND owner_id = ?";
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, id);
      stmt.setInt(2, ownerId);
      return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
      System.err.println("[update ERROR] " + e.getMessage());
      return false;
    }
  }

  @Override
  public Collection<City> findAll() {
    List<City> cities = new ArrayList<>();
    String sql = "SELECT * FROM cities";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        City city = new City();
        city.setOwnerId(rs.getInt("owner_id"));
        city.setId((int) rs.getInt("id"));
        city.setName(rs.getString("name"));
        city.setCoordinates(
            new Coordinates(rs.getDouble("coordinates_x"), rs.getInt("coordinates_y")));
        city.setCreationDate(rs.getTimestamp("creation_date").toLocalDateTime());
        city.setArea(rs.getLong("area"));
        city.setPopulation(rs.getInt("population"));
        city.setMetersAboveSeaLevel(rs.getFloat("meters_above_sea_level"));

        city.setClimate(Climate.valueOf(rs.getString("climate")));
        city.setGovernment(Government.valueOf(rs.getString("government")));
        city.setStandardOfLiving(StandardOfLiving.valueOf(rs.getString("standard_of_living")));

        float height = rs.getFloat("governor_height");
        Human governor = (rs.wasNull()) ? null : new Human(height);
        city.setGovernor(governor);

        cities.add(city);
      }

    } catch (SQLException e) {
      System.err.println("[findAll ERROR] " + e.getMessage());
    }

    return cities;
  }

  @Override
  public City findById(int id) {
    // TODO
    return null;
  }

  @Override
  public boolean deleteAllByOwner(int ownerId) {
    String sql = "DELETE FROM cities WHERE owner_id = ?";
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, ownerId);
      int affected = stmt.executeUpdate();
      return affected >= 0;

    } catch (SQLException e) {
      System.err.println("[deleteAllByOwner ERROR] " + e.getMessage());
      return false;
    }
  }

  @Override
  public boolean deleteByIdsAndOwner(List<Integer> ids, int ownerId) {
    if (ids == null || ids.isEmpty()) return true;

    String sql =
        """
        DELETE FROM cities
        WHERE owner_id = ? AND id = ANY (?)
    """;

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, ownerId);

      Integer[] idArray = ids.toArray(new Integer[0]);
      Array sqlArray = conn.createArrayOf("INTEGER", idArray);
      stmt.setArray(2, sqlArray);

      int affected = stmt.executeUpdate();
      return affected >= 0;

    } catch (SQLException e) {
      System.err.println("[deleteByIdsAndOwner ERROR] " + e.getMessage());
      return false;
    }
  }
}
