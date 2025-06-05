package client_command;

import input.InputLoopHandler;
import network.CommandRequest;
import network.CommandSender;
import model.City;
import data.DataReader;

import java.util.Map;
import java.util.Scanner;
import java.util.HashMap;

public class ClientCommandProcessor {
  private final CommandSender sender;
  private final Scanner scanner;
  private final DataReader<City> reader;
  private final Map<String, Runnable> localCommands = new HashMap<>();

  private InputLoopHandler loop;

  public ClientCommandProcessor(CommandSender sender, Scanner scanner, DataReader<City> reader) {
    this.sender = sender;
    this.scanner = scanner;
    this.reader = reader;
    registerLocalCommands();
  }

  public void bindLoop(InputLoopHandler loop) {
    this.loop = loop;
  }

  private void registerLocalCommands() {
    localCommands.put("login", () -> new LoginCommand(sender).execute(scanner));
    localCommands.put("register", () -> new RegisterCommand(sender).execute(scanner));
    localCommands.put("logout", () -> new LogoutCommand().execute());
    //TODO
    //localCommands.put("help", () -> System.out.println("Локальные команды: login, register, logout, exit, execute_script")); - переделать
    localCommands.put("exit", () -> {
      System.out.println("Завершение работы клиента...");
      sender.send(new CommandRequest("exit"));
      if (loop != null) loop.stop();
    });
    localCommands.put("execute_script", () -> new ExecuteScriptCommand(this, sender, reader).execute(scanner));
  }

  public void process(String inputLine) {
    String commandName = inputLine.trim().split("\\s+")[0].toLowerCase();

    Runnable local = localCommands.get(commandName);
    if (local != null) {
      local.run();
      return;
    }
    try {
      CommandRequest request = CommandBuilder.build(inputLine, reader);
      sender.send(request);
    } catch (Exception e) {
      System.out.println("[CLIENT] Ошибка: " + e.getMessage());
    }
  }
}
