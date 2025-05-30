package client_command;

import city.City;
import data.ConsoleDataReader;
import data.DataReader;
import data.FileDataReader;
import java.io.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Consumer;

/** Обработчик клиентских команд */
public class CommandHandler {
  private final Scanner scanner;
  private boolean isScriptMode = false;
  private BufferedReader scriptReader;
  private final Set<String> activeScripts = new HashSet<>();
  private final Consumer<String> commandConsumer;

  private final ConsoleDataReader<City> consoleReader;
  private FileDataReader<City> fileReader;
  private DataReader<City> activeReader;

  public CommandHandler(Scanner scanner, Consumer<String> commandConsumer) {
    this.scanner = scanner;
    this.commandConsumer = commandConsumer;
    this.consoleReader = new ConsoleDataReader<>(scanner);
    this.activeReader = consoleReader;
  }

  public void handleInputLoop() {
    while (true) {
      System.out.print("> ");
      String input = readLine().trim();
      if (input.isEmpty()) continue;

      if (input.startsWith("execute_script")) {
        executeScript(input);
      } else {
        commandConsumer.accept(input);
      }
    }
  }

  private void executeScript(String input) {
    String[] args = input.trim().split("\\s+");
    if (args.length < 2) {
      System.out.println("Ошибка: нужно указать имя файла.");
      return;
    }

    String fileName = args[1];
    File file = new File(fileName).getAbsoluteFile();

    if (!file.exists() || !file.canRead()) {
      System.out.println("Ошибка: файл не найден или недоступен.");
      return;
    }

    if (activeScripts.contains(file.getAbsolutePath())) {
      System.out.println("Ошибка: рекурсивное выполнение скрипта обнаружено — " + fileName);
      return;
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      activeScripts.add(file.getAbsolutePath());
      setScriptMode(true, reader);

      String line;
      while ((line = readLine()) != null) {
        line = line.trim();
        if (!line.isEmpty()) {
          if (line.startsWith("execute_script")) {
            executeScript(line);
          } else {
            commandConsumer.accept(line);
          }
        }
      }

    } catch (IOException e) {
      System.out.println("Ошибка при чтении скрипта: " + e.getMessage());
    } finally {
      activeScripts.remove(file.getAbsolutePath());
      setScriptMode(false, null);
    }
  }

  private String readLine() {
    try {
      if (isScriptMode && scriptReader != null) {
        return scriptReader.readLine();
      } else {
        return scanner.nextLine();
      }
    } catch (IOException e) {
      System.out.println("Ошибка при чтении ввода.");
      return "";
    }
  }

  private void setScriptMode(boolean scriptMode, BufferedReader reader) {
    this.isScriptMode = scriptMode;
    this.scriptReader = reader;

    if (scriptMode && reader != null) {
      this.fileReader = new FileDataReader<>(reader);
      this.activeReader = fileReader;
    } else {
      this.activeReader = consoleReader;
    }
  }

  public DataReader<City> getActiveReader() {
    return activeReader;
  }
}
