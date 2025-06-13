package client_command;

import session.CurrentSession;

public class LogoutCommand implements ClientCommand {

  @Override
  public void execute(String[] args) {
    if (!CurrentSession.isLoggedIn()) {
      System.out.println("[CLIENT] Вы не авторизованы.");
      return;
    }

    String user = CurrentSession.getUsername();
    CurrentSession.logout();
    System.out.println("[CLIENT] Пользователь '" + user + "' вышел из системы.");
  }

  @Override
  public String getName() {
    return "logout";
  }
}
