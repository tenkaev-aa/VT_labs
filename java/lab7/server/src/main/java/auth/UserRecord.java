package auth;

public class UserRecord {
  private final int id;
  private final String passwordHash;
  private final String saltHex;

  public UserRecord(int id, String passwordHash, String saltHex) {
    this.id = id;
    this.passwordHash = passwordHash;
    this.saltHex = saltHex;
  }

  public int getId() {
    return id;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public String getSaltHex() {
    return saltHex;
  }
}
