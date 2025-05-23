package network;

import client_command.CommandBuilder;
import client_command.CommandHandler;
import util.EnvReader;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class UDPClient {
  private static final int SERVER_PORT = EnvReader.getPort("SERVER_PORT", 1488);
  private static final String SERVER_HOST = "localhost";

  public static void main(String[] args) {
    try {
      Scanner scanner = new Scanner(System.in);
      CommandSender sender = new CommandSender(SERVER_HOST, SERVER_PORT);

        AtomicReference<CommandHandler> handlerRef = new AtomicReference<>();

        handlerRef.set(new CommandHandler(scanner, line -> {
            if (line.equalsIgnoreCase("exit")) {
                System.out.println("Завершение работы клиента.");
                sender.send(new CommandRequest("exit"));
                System.exit(0);
            }

            try {
                CommandRequest request = CommandBuilder.build(line, handlerRef.get().getActiveReader());
                sender.send(request);
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }));

        handlerRef.get().handleInputLoop();

    } catch (IOException e) {
      System.out.println("Ошибка клиента: " + e.getMessage());
    }
  }
}
