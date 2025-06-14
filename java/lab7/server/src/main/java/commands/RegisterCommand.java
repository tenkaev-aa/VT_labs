package commands;

import auth.PasswordHasher;
import database.dao.UserDAO;
import network.CommandRequest;
import network.CommandResponse;

public class RegisterCommand implements Command {
  private final UserDAO userDAO;

  public RegisterCommand(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  @Override
  public CommandResponse execute(CommandRequest request) {
    String username = request.getUsername();
    String password = request.getPassword();

    if (username == null || password == null || username.isBlank() || password.isBlank()) {
      return new CommandResponse("Ошибка: укажите имя и пароль");
    }

    byte[] salt = PasswordHasher.generateSalt();
    String hash = PasswordHasher.hash(password, salt);
    String saltHex = PasswordHasher.bytesToHex(salt);

    boolean success = userDAO.register(username, hash, saltHex);

    if (success) {
      return new CommandResponse("Регистрация прошла успешно");
    } else {
      return new CommandResponse("Пользователь с таким именем уже существует");
    }
  }

  @Override
  public String getDescription() {
    return "команда для регистрации пользователя";
  }

  @Override
  public boolean isInternalOnly() {
    return true;
  }
}
