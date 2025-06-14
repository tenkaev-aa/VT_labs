package commands;

import auth.PasswordHasher;
import auth.UserRecord;
import database.dao.UserDAO;
import network.CommandRequest;
import network.CommandResponse;

public class LoginCommand implements Command {
  private final UserDAO userDAO;

  public LoginCommand(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  @Override
  public CommandResponse execute(CommandRequest request) {
    String username = request.getUsername();
    String password = request.getPassword();

    if (username == null || password == null || username.isBlank() || password.isBlank()) {
      return new CommandResponse("Ошибка: укажите имя и пароль");
    }

    UserRecord user = userDAO.getUserRecord(username);
    if (user == null) {
      return new CommandResponse("Неверный логин или пароль");
    }

    byte[] salt = PasswordHasher.hexToBytes(user.getSaltHex());
    String computedHash = PasswordHasher.hash(password, salt);

    if (computedHash.equals(user.getPasswordHash())) {
      return new CommandResponse("Вход выполнен успешно");
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
