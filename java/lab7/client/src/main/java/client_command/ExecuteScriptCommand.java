package client_command;

import data.DataReader;
import data.FileDataReader;
import java.io.*;
import java.util.*;
import model.City;
import session.CurrentSession;

public class ExecuteScriptCommand implements ClientCommand {
  private final ClientCommandProcessor processor;
  private final DataReader<City> reader;
  private final Set<String> activeScripts = new HashSet<>();

  public ExecuteScriptCommand(ClientCommandProcessor processor, DataReader<City> reader) {
    this.processor = processor;
    this.reader = reader;
  }

  @Override
  public void execute(String[] args, DataReader<City> reader) {
    if (!CurrentSession.isLoggedIn()) {
      System.out.println("[CLIENT] Неизвестная команда: execute_script");
      return;
    }
    if (args.length == 0) {
      System.out.print("Введите имя файла скрипта: ");
      Scanner scanner = new Scanner(System.in);
      String filename = scanner.nextLine().trim();
      executeFromFile(new File(filename).getAbsoluteFile(), new HashSet<>());
    } else {
      File file = new File(args[0]).getAbsoluteFile();
      executeFromFile(file, new HashSet<>());
    }
  }

  private void executeFromFile(File file, Set<String> inheritedScripts) {
    try {
      String canonicalPath = file.getCanonicalPath();
      if (inheritedScripts.contains(canonicalPath)) {
        System.out.println("[CLIENT] Рекурсивное выполнение скрипта запрещено: " + file.getName());
        return;
      }

      if (!file.exists() || !file.canRead()) {
        System.out.println("[CLIENT] Файл не найден или недоступен: " + file);
        return;
      }

      System.out.println("[CLIENT] Выполнение скрипта: " + file.getName());

      Set<String> updatedScripts = new HashSet<>(inheritedScripts);
      updatedScripts.add(canonicalPath);

      try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        FileDataReader<City> scriptReader = new FileDataReader<>(reader);
        String line;
        while ((line = reader.readLine()) != null) {
          line = line.trim();
          if (line.isEmpty()) continue;

          if (line.toLowerCase().startsWith("execute_script")) {
            String[] parts = line.split("\\s+");
            if (parts.length < 2) {
              System.out.println("[CLIENT] Ошибка: имя вложенного скрипта не указано.");
              continue;
            }
            File nested = new File(parts[1]).getAbsoluteFile();
            executeFromFile(nested, updatedScripts);
          } else {
            processor.process(line, scriptReader);
          }
        }
      }
    } catch (IOException e) {
      System.out.println("[CLIENT] Ошибка выполнения скрипта: " + e.getMessage());
    }
  }

  @Override
  public String getName() {
    return "execute_script";
  }
}
