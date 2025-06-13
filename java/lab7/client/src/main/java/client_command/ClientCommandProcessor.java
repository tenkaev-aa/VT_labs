package client_command;

import data.DataReader;
import input.InputLoopHandler;
import java.util.Arrays;
import java.util.Scanner;
import model.City;
import network.CommandRequest;
import network.CommandResponse;
import network.CommandSender;
import session.CurrentSession;

public class ClientCommandProcessor {
  private final ClientCommandRegistry registry;
  private final CommandSender sender;

  public ClientCommandProcessor(
      CommandSender sender, Scanner scanner, DataReader<City> reader, InputLoopHandler loop) {
    this.sender = sender;
    this.registry = new ClientCommandRegistry(sender, reader, loop, scanner, this);
  }

  public void process(String inputLine, DataReader<City> reader) {
    if (inputLine == null || inputLine.isBlank()) return;

    String[] parts = inputLine.trim().split("\\s+");
    String commandName = parts[0].toLowerCase();
    String[] args = Arrays.copyOfRange(parts, 1, parts.length);

    ClientCommand command = registry.get(commandName);
    if (command != null) {
      try {
        command.execute(args, reader);
      } catch (Exception e) {
        System.out.println("[CLIENT] Ошибка выполнения команды: " + e.getMessage());
      }
      return;
    }

    try {
      CommandRequest request =
          new CommandRequest(
              commandName, args, null, CurrentSession.getUsername(), CurrentSession.getPassword());

      CommandResponse response = sender.send(request);

    } catch (Exception e) {
      System.out.println("[CLIENT] Ошибка при отправке команды на сервер: " + e.getMessage());
    }
  }
}
