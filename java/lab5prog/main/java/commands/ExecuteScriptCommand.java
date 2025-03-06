package commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Команда для выполнения скрипта из файла.
 *
 * <p>Эта команда позволяет выполнять набор команд, записанных в текстовом файле. Она поддерживает
 * защиту от рекурсии, предотвращая выполнение одного и того же скрипта более одного раза в рамках
 * одного вызова.
 *
 * <p>Команда принимает имя файла в качестве аргумента и выполняет все команды, содержащиеся в этом
 * файле, последовательно.
 *
 * @see Command
 * @see CommandHandler
 */
public class ExecuteScriptCommand implements Command {
  private final CommandHandler commandHandler;
  private final Set<String> activeScripts = new HashSet<>();

  /**
   * Создает команду для выполнения скрипта.
   *
   * @param commandHandler обработчик команд, который будет использоваться для выполнения команд из
   *     скрипта.
   */
  public ExecuteScriptCommand(CommandHandler commandHandler) {
    this.commandHandler = commandHandler;
  }

  /**
   * Выполняет команду, запуская скрипт из указанного файла.
   *
   * <p>Метод проверяет, что файл существует и не вызывает рекурсии. Если файл не найден, выводится
   * сообщение об ошибке. Если обнаружена рекурсия (например, скрипт вызывает сам себя), выполнение
   * прерывается.
   *
   * @param args аргументы команды. Ожидается, что второй аргумент (args[1]) содержит имя файла
   *     скрипта.
   */
  @Override
  public void execute(String[] args) {
    if (args.length < 2) {
      System.out.println("Ошибка: нужно вписать filename");
      return;
    }

    String fileName = args[1];
    File scriptFile = new File(fileName).getAbsoluteFile();

    if (activeScripts.contains(scriptFile.getAbsolutePath())) {
      System.out.println("Ошибка: обнаружена рекурсия в скрипте " + fileName);
      return;
    }

    activeScripts.add(scriptFile.getAbsolutePath());

    try (Scanner fileScanner = new Scanner(scriptFile)) {
      while (fileScanner.hasNextLine()) {
        String command = fileScanner.nextLine().trim();
        if (!command.isEmpty()) {
          System.out.println("Выполнение команды: " + command);
          commandHandler.handleCommand(command);
        }
      }
    } catch (FileNotFoundException e) {
      System.out.println("Файл скрипта не найден: " + fileName);
    } finally {
      activeScripts.remove(scriptFile.getAbsolutePath());
    }
  }

  /**
   * Возвращает описание команды.
   *
   * @return описание команды.
   */
  @Override
  public String getDescription() {
    return "выполнить скрипт из файла";
  }
}
