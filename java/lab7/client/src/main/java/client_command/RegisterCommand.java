package client_command;

import network.CommandRequest;
import network.CommandResponse;
import network.CommandSender;
import session.CurrentSession;

import java.util.Scanner;

public class RegisterCommand {
  private final CommandSender sender;

  public RegisterCommand(CommandSender sender) {
    this.sender = sender;
  }

  public void execute(Scanner scanner) {
    System.out.print("Логин: ");
    String username = scanner.nextLine().trim();

    System.out.print("Пароль: ");
    String password = scanner.nextLine().trim();

    CommandRequest request = new CommandRequest(
        "register",
        null,
        null,
        username,
        password
    );

    CommandResponse response = sender.send(request);
    System.out.println("[CLIENT] " + response.getMessage());

    if (response.getMessage().toLowerCase().contains("успешно")) {
      CurrentSession.login(username, password);
      System.out.println("[CLIENT] Вход выполнен как: " + username);
    }
  }
}
