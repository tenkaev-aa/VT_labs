package commands;

import auth.UserRecord;
import database.dao.UserDAO;
import java.util.Collections;
import network.CommandRequest;
import network.CommandResponse;
import session.SessionManager;

public class LoginCommand implements Command {
  private final UserDAO userDAO;

  public LoginCommand(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  @Override
  public CommandResponse execute(CommandRequest request) {
    String username = request.getUsername().orElse(null);
    String providedHash = request.getPassword().orElse(null);

    if (username == null || providedHash == null) {
      return new CommandResponse("Ошибка: укажите имя и  пароль.");
    }

    UserRecord user = userDAO.getUserRecord(username);
    if (user == null) {
      return new CommandResponse("Неверный логин или пароль");
    }

    String storedHash = user.passwordHash();
    if (providedHash.equals(storedHash)) {
      String token = SessionManager.createToken(username);
      return new CommandResponse("Вход выполнен успешно", Collections.emptyList(), token);
    } else {
      return new CommandResponse("Неверный логин или пароль");
    }
  }

  @Override
  public String getDescription() {
    return "команда для входа в профиль пользователя";
  }

  @Override
  public boolean isInternalOnly() {
    return true;
  }
}
