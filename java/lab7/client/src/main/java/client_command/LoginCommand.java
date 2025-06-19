package client_command;

import data.DataReader;
import java.io.Console;
import java.util.Optional;
import java.util.Scanner;
import model.City;
import network.CommandRequest;
import network.CommandResponse;
import network.CommandSender;
import network.PasswordHasher;
import session.CurrentSession;

public class LoginCommand implements ClientCommand {
  private final CommandSender sender;

  public LoginCommand(CommandSender sender) {
    this.sender = sender;
  }

  @Override
  public void execute(String[] args, DataReader<City> reader) {
    Scanner scanner = new Scanner(System.in);

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

    CommandRequest getSaltRequest =
        new CommandRequest("get_salt", new String[] {username}, null, null, null);
    CommandResponse saltResponse = sender.send(getSaltRequest);

    if (saltResponse == null || saltResponse.getMessage() == null) {
      System.out.println("[CLIENT] Ошибка: не удалось получить соль с сервера.");
      return;
    }

    String saltHex = saltResponse.getMessage().trim();

    if (saltHex.equalsIgnoreCase("null") || saltHex.toLowerCase().contains("ошибка")) {
      System.out.println("[CLIENT] Ошибка: " + saltHex);
      return;
    }

    byte[] salt;
    try {
      salt = PasswordHasher.hexToBytes(saltHex);
    } catch (Exception e) {
      System.out.println("[CLIENT] Ошибка: пользователь не найден");
      return;
    }

    String hashedPassword = PasswordHasher.hash(password, salt);

    CommandRequest loginRequest = new CommandRequest("login", null, null, username, hashedPassword);

    CommandResponse response = sender.send(loginRequest);
    if (response == null) {
      System.out.println("[CLIENT] Ошибка: сервер не ответил на запрос входа.");
      return;
    }

    if (response.getMessage().toLowerCase().contains("успешно")) {
      Optional<String> tokenOpt = response.getSessionToken();
      if (tokenOpt.isPresent()) {
        CurrentSession.login(username, password, tokenOpt.get());
        System.out.println("[CLIENT] Вход выполнен как: " + username);
      } else {
        System.out.println("[CLIENT] Ошибка: токен не получен от сервера.");
      }
    }
  }

  @Override
  public String getName() {
    return "login";
  }
}
