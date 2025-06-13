package session;

public class CurrentSession {
  private static String username;
  private static String password;

  public static void login(String user, String pass) {
    username = user;
    password = pass;
  }

  public static void logout() {
    username = null;
    password = null;
  }

  public static String getUsername() {
    return username;
  }

  public static String getPassword() {
    return password;
  }

  public static boolean isLoggedIn() {
    return username != null && password != null;
  }
}
