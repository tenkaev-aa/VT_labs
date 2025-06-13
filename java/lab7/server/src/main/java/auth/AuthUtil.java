package auth;

import database.dao.UserDAO;
import network.CommandRequest;

public class AuthUtil {

  /**
   * Проверяет логин и пароль. Возвращает id пользователя или -1, если не удалось авторизоваться.
   */
  public static int authorizeAndGetUserId(CommandRequest request, UserDAO userDAO) {
    String username = request.getUsername();
    String password = request.getPassword();
    if (username == null || password == null) return -1;

    UserRecord user = userDAO.getUserRecord(username);
    if (user == null) return -1;

    byte[] salt = PasswordHasher.hexToBytes(user.getSaltHex());
    String hashedPassword = PasswordHasher.hash(password, salt);

    if (!hashedPassword.equals(user.getPasswordHash())) return -1;

    return user.getId();
  }
}
