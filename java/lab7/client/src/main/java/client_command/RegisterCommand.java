package client_command;

import data.DataReader;
import java.io.Console;
import java.util.Scanner;
import model.City;
import network.CommandRequest;
import network.CommandResponse;
import network.CommandSender;
import network.PasswordHasher;
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
    String username;
    String password;

    Console console = System.console();
    if (console != null) {
      username = console.readLine("Логин: ").trim();
      char[] pwdChars = console.readPassword("Пароль: ");
      password = new String(pwdChars);
    } else {
      System.out.print("Логин: ");
      username = scanner.nextLine().trim();
      System.out.print("Пароль: ");
      password = scanner.nextLine().trim();
    }

    byte[] salt = PasswordHasher.getSalt();
    String saltHex = PasswordHasher.bytesToHex(salt);
    String hashed = PasswordHasher.hash(password, salt);

    CommandRequest request =
        new CommandRequest("register", new String[] {username, saltHex, hashed}, null, null, null);

    CommandResponse response = sender.send(request);

    if (response.getMessage().toLowerCase().contains("успешно")) {
      CurrentSession.login(username, password, response.getSessionToken().orElse(null));
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
