package client_command;

import data.DataReader;
import java.util.Scanner;
import model.City;
import network.CommandRequest;
import network.CommandResponse;
import network.CommandSender;
import session.CurrentSession;

public class RegisterCommand implements ClientCommand {
  private final CommandSender sender;
  private final Scanner scanner;

  public RegisterCommand(CommandSender sender, Scanner scanner) {
    this.sender = sender;
    this.scanner = scanner;
  }

  @Override
  public void execute(String[] args, DataReader<City> reader) {
    System.out.print("Логин: ");
    String username = scanner.nextLine().trim();

    System.out.print("Пароль: ");
    String password = scanner.nextLine().trim();

    CommandRequest request =
        new CommandRequest("register", new String[0], null, username, password);

    CommandResponse response = sender.send(request);

    if (response.getMessage().toLowerCase().contains("успешно")) {
      CurrentSession.login(username, password);
      System.out.println("[CLIENT] Вход выполнен как: " + username);
    } else {
      System.out.println("[CLIENT] " + response.getMessage());
    }
  }

  @Override
  public String getName() {
    return "register";
  }
}
