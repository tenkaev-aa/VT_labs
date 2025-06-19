package auth;

import database.dao.UserDAO;
import network.CommandRequest;
import network.PasswordHasher;
import session.SessionManager;

public class AuthUtil {

  /**
   * Проверяет авторизацию по sessionToken или по username+password (только для login/register).
   *
   * @param request входящий запрос
   * @param userDAO доступ к базе
   * @return userId если успешная авторизация, иначе -1
   */
  public static int authorizeAndGetUserId(CommandRequest request, UserDAO userDAO) {
    String token = request.getSessionToken().orElse(null);
    if (token != null) {
      Integer userId = SessionManager.getUserIdByToken(token, userDAO);
      return userId != null ? userId : -1;
    }

    String username = request.getUsername().orElse(null);
    String password = request.getPassword().orElse(null);

    if (username == null || password == null) return -1;

    UserRecord user = userDAO.getUserRecord(username);
    if (user == null) return -1;

    byte[] salt = PasswordHasher.hexToBytes(user.saltHex());
    String hashedPassword = PasswordHasher.hash(password, salt);

    if (!hashedPassword.equals(user.passwordHash())) return -1;

    return user.id();
  }

  public static boolean authorizeToken(CommandRequest request) {
    return request.getSessionToken().isPresent()
        && SessionManager.isValid(request.getSessionToken().get());
  }

  public static String getUsernameByToken(CommandRequest request) {
    return request
        .getSessionToken()
        .map(SessionManager::getUsername)
        .orElseThrow(() -> new IllegalStateException("Токен отсутствует в запросе"));
  }
}
