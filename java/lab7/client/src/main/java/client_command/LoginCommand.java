package client_command;

import data.DataReader;
import java.util.Scanner;
import model.City;
import network.CommandRequest;
import network.CommandResponse;
import network.CommandSender;
import session.CurrentSession;

public class LoginCommand implements ClientCommand {
  private final CommandSender sender;

  public LoginCommand(CommandSender sender) {
    this.sender = sender;
  }

  @Override
  public void execute(String[] args, DataReader<City> reader) {
    Scanner scanner = new Scanner(System.in);

    System.out.print("Логин: ");
    String username = scanner.nextLine().trim();

    System.out.print("Пароль: ");
    String password = scanner.nextLine().trim();

    CommandRequest request =
        new CommandRequest("login", new String[] {username, password}, null, username, password);

    CommandResponse response = sender.send(request);

    if (response.getMessage().toLowerCase().contains("успешно")) {
      CurrentSession.login(username, password);
      System.out.println("[CLIENT] Вход выполнен как: " + username);
    }
  }

  @Override
  public String getName() {
    return "login";
  }
}
