package client_command;

import data.DataReader;
import input.InputLoopHandler;
import java.util.Arrays;
import java.util.Scanner;
import model.City;
import network.CommandSender;

public class ClientCommandProcessor {
  private final ClientCommandRegistry registry;

  public ClientCommandProcessor(
      CommandSender sender, Scanner scanner, DataReader<City> reader, InputLoopHandler loop) {
    this.registry = new ClientCommandRegistry(sender, reader, loop, scanner, this);
  }

  public void process(String inputLine) {
    if (inputLine == null || inputLine.isBlank()) return;

    String[] parts = inputLine.trim().split("\\s+");
    String commandName = parts[0].toLowerCase();
    String[] args = Arrays.copyOfRange(parts, 1, parts.length);

    ClientCommand command = registry.get(commandName);
    if (command != null) {
      try {
        command.execute(args);
      } catch (Exception e) {
        System.out.println("[CLIENT] Ошибка выполнения команды: " + e.getMessage());
      }
    } else {
      System.out.println("[CLIENT] Неизвестная команда: " + commandName);
    }
  }
}
