package commands;

import database.dao.UserDAO;
import network.CommandRequest;
import network.CommandResponse;
import session.SessionManager;

public class RegisterCommand implements Command {
  private final UserDAO userDAO;

  public RegisterCommand(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  @Override
  public CommandResponse execute(CommandRequest request) {

    String[] args = request.getArguments();
    String username = args[0];
    String saltHex = args[1];
    String hashedPassword = args[2];

    if (username == null || saltHex == null || hashedPassword == null) {
      return new CommandResponse("Ошибка: укажите имя и пароль");
    }

    boolean success = userDAO.register(username, hashedPassword, saltHex);

    if (success) {
      String token = SessionManager.createToken(username);
      return new CommandResponse("Регистрация прошла успешно", token);
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
