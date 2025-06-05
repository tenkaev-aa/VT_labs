package client_command;


import data.DataReader;
import model.City;
import network.CommandSender;
import java.io.*;
import java.util.*;

public class ExecuteScriptCommand {
  private final ClientCommandProcessor processor;
  private final File rootFile;
  private final Set<String> activeScripts = new HashSet<>();
  private final DataReader<City> reader;

  public ExecuteScriptCommand(ClientCommandProcessor processor, CommandSender sender, DataReader<City> reader) {
    this.processor = processor;
    this.reader = reader;
    this.rootFile = null;
  }

  private ExecuteScriptCommand(ClientCommandProcessor processor, DataReader<City> reader, File currentFile, Set<String> inheritedScripts) {
    this.processor = processor;
    this.reader = reader;
    this.rootFile = currentFile;
    this.activeScripts.addAll(inheritedScripts);
  }

  public void execute(Scanner scanner) {
    System.out.print("Введите имя файла скрипта: ");
    String filename = scanner.nextLine().trim();
    File file = new File(filename).getAbsoluteFile();
    executeFromFile(file);
  }

  private void executeFromFile(File file) {
    if (!file.exists() || !file.canRead()) {
      System.out.println("[CLIENT] Файл не найден или недоступен: " + file);
      return;
    }

    String canonicalPath;
    try {
      canonicalPath = file.getCanonicalPath();
    } catch (IOException e) {
      System.out.println("[CLIENT] Ошибка чтения пути: " + e.getMessage());
      return;
    }

    if (activeScripts.contains(canonicalPath)) {
      System.out.println("[CLIENT] Рекурсивное выполнение скрипта запрещено: " + file.getName());
      return;
    }

    System.out.println("[CLIENT] Выполнение скрипта: " + file.getName());

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      activeScripts.add(canonicalPath);
      String line;
      while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (!line.isEmpty()) {
          if (line.toLowerCase().startsWith("execute_script")) {
            String[] parts = line.split("\\s+");
            if (parts.length < 2) {
              System.out.println("[CLIENT] Ошибка: имя вложенного скрипта не указано.");
              continue;
            }
            File nested = new File(parts[1]).getAbsoluteFile();
            new ExecuteScriptCommand(processor, this.reader, nested, activeScripts).executeFromFile(nested);
          } else {
            processor.process(line);
          }
        }
      }
    } catch (IOException e) {
      System.out.println("[CLIENT] Ошибка выполнения скрипта: " + e.getMessage());
    } finally {
      activeScripts.remove(canonicalPath);
    }
  }
}
