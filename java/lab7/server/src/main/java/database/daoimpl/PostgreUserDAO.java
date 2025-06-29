package database.daoimpl;

import auth.UserRecord;
import database.DatabaseManager;
import database.dao.UserDAO;
import java.sql.*;

public class PostgreUserDAO implements UserDAO {
  @Override
  public boolean register(String username, String passwordHash, String saltHex) {
    String query = "INSERT INTO users (username, password_hash, salt) VALUES (?, ?, ?)";
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)) {

      stmt.setString(1, username);
      stmt.setString(2, passwordHash);
      stmt.setString(3, saltHex);
      stmt.executeUpdate();
      return true;

    } catch (SQLException e) {
      System.err.println("[REGISTER ERROR] " + e.getMessage());
      return false;
    }
  }

  @Override
  public boolean checkCredentials(String username, String passwordHash) {
    String query = "SELECT 1 FROM users WHERE username = ? AND password_hash = ?";
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)) {

      stmt.setString(1, username);
      stmt.setString(2, passwordHash);

      ResultSet rs = stmt.executeQuery();
      return rs.next();

    } catch (SQLException e) {
      System.err.println("[CHECK CREDENTIALS ERROR] " + e.getMessage());
      return false;
    }
  }

  @Override
  public UserRecord getUserRecord(String username) {
    String query = "SELECT id, password_hash, salt FROM users WHERE username = ?";
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)) {

      stmt.setString(1, username);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          int id = rs.getInt("id");
          String hash = rs.getString("password_hash");
          String salt = rs.getString("salt");
          return new UserRecord(id, hash, salt);
        }
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return null;
  }

  @Override
  public String getUsername(int userId) {
    String query = "SELECT username FROM users WHERE id = ?";
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)) {

      stmt.setInt(1, userId);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return rs.getString("username");
      }

    } catch (SQLException e) {
      System.err.println("[GET USERNAME ERROR] " + e.getMessage());
    }

    return null;
  }

  @Override
  public int getUserId(String username) {
    UserRecord user = getUserRecord(username);
    return user != null ? user.id() : -1;
  }
}
