package input;

import client_command.ClientCommandProcessor;
import data.ConsoleDataReader;
import data.DataReader;
import java.io.IOException;
import java.util.*;
import model.City;

public class InputLoopHandler {
  private final Scanner scanner;
  private ClientCommandProcessor processor;

  private final ConsoleDataReader<City> consoleReader;
  private DataReader<City> activeReader;
  private boolean running = true;

  public InputLoopHandler(Scanner scanner) {
    this.scanner = scanner;
    this.consoleReader = new ConsoleDataReader<>(scanner);
    this.activeReader = consoleReader;
  }

  public void start() throws IOException {
    while (running) {
      System.out.print("> ");
      String input = readLine().trim();
      if (!input.isEmpty()) {
        processor.process(input);
      }
    }
  }

  public void bindProcessor(ClientCommandProcessor processor) {
    this.processor = processor;
  }

  public void stop() {
    this.running = false;
  }

  private String readLine() {
    try {
      return scanner.nextLine();
    } catch (Exception e) {
      System.out.println("[CLIENT] Ошибка при чтении ввода: " + e.getMessage());
      return "";
    }
  }

  public DataReader<City> getActiveReader() {
    return activeReader;
  }
}
