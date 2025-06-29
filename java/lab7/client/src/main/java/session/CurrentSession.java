package session;

import java.util.Optional;

public class CurrentSession {
  private static String username;
  private static String password;
  private static String token;

  public static void login(String user, String pass, String sessionToken) {
    username = user;
    password = pass;
    token = sessionToken;
  }

  public static Optional<String> getToken() {
    return Optional.ofNullable(token);
  }

  public static void logout() {
    username = null;
    password = null;
    token = null;
  }

  public static Optional<String> getUsername() {
    return Optional.ofNullable(username);
  }

  public static Optional<String> getPassword() {
    return Optional.ofNullable(password);
  }

  public static boolean isLoggedIn() {
    return token != null;
  }
}
