package commands;

import database.dao.UserDAO;
import network.CommandRequest;
import network.CommandResponse;

public class GetSaltCommand implements Command {

  private final UserDAO userDAO;

  public GetSaltCommand(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  @Override
  public CommandResponse execute(CommandRequest request) {
    String[] args = request.getArguments();
    if (args.length < 1) {
      return new CommandResponse("Ошибка: укажите имя пользователя.");
    }

    String username = args[0];
    var user = userDAO.getUserRecord(username);
    if (user == null) {
      return new CommandResponse("Пользователь не найден.");
    }

    return new CommandResponse(user.saltHex());
  }

  @Override
  public String getDescription() {
    return "получить соль для пользователя";
  }

  @Override
  public boolean isHidden() {
    return true;
  }
}
