package session;

import database.dao.UserDAO;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
  private static final Map<String, String> tokenToUser = new ConcurrentHashMap<>();

  public static String createToken(String username) {
    String token = UUID.randomUUID().toString();
    tokenToUser.put(token, username);
    return token;
  }

  public static String getUsername(String token) {
    String user = tokenToUser.get(token);
    return user;
  }

  public static boolean isValid(String token) {
    return tokenToUser.containsKey(token);
  }

  public static void invalidate(String token) {
    tokenToUser.remove(token);
  }

  public static Integer getUserIdByToken(String token, UserDAO userDAO) {
    String username = getUsername(token);

    if (username == null) return null;

    int id = userDAO.getUserId(username);

    return id;
  }
}
