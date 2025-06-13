package database.dao;

import auth.UserRecord;

public interface UserDAO {
  boolean register(String username, String passwordHash, String saltHex);

  boolean checkCredentials(String username, String passwordHash);

  UserRecord getUserRecord(String username);
}
