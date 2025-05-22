package network;

import client_command.CommandBuilder;
import client_command.CommandHandler;
import java.io.IOException;
import java.util.Scanner;

public class UDPClient {
  private static final int SERVER_PORT = 1488;
  private static final String SERVER_HOST = "localhost";

  public static void main(String[] args) {
    try {
      Scanner scanner = new Scanner(System.in);
      CommandSender sender = new CommandSender(SERVER_HOST, SERVER_PORT);

      final CommandHandler[] handlerRef = new CommandHandler[1]; // обёртка
      CommandRequest connect = new CommandRequest("connect", true);
      sender.send(connect);

      handlerRef[0] =
          new CommandHandler(
              scanner,
              (String line) -> {
                if (line.equalsIgnoreCase("exit")) {
                  System.out.println("Завершение работы клиента.");
                  CommandRequest exit = new CommandRequest("exit", true);
                  sender.send(exit);
                  System.exit(0);
                }

                try {
                  CommandRequest request =
                      CommandBuilder.build(line, handlerRef[0].getActiveReader());
                  sender.send(request);
                } catch (Exception e) {
                  System.out.println("Ошибка: " + e.getMessage());
                }
              });

      handlerRef[0].handleInputLoop();

    } catch (IOException e) {
      System.out.println("Ошибка клиента: " + e.getMessage());
    }
  }
}
