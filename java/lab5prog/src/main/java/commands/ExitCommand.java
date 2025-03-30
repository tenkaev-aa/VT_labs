package commands;

/**
 * Команда для завершения программы.
 *
 * <p>Эта команда завершает выполнение программы с кодом выхода {@code 0}, что означает успешное
 * завершение. Перед завершением программа выводит сообщение о завершении.
 *
 * @see Command
 */
public class ExitCommand implements Command {

  /**
   * Выполняет команду, завершая программу.
   *
   * <p>Метод выводит сообщение "Завершение программы" и завершает выполнение программы с помощью
   * {@link System#exit(int)}.
   *
   * @param args аргументы команды (в данной команде не используются).
   */
  @Override
  public void execute(String[] args) {
    System.out.println("Завершение программы.");
    System.exit(0);
  }

  /**
   * Возвращает описание команды.
   *
   * @return описание команды.
   */
  @Override
  public String getDescription() {
    return "завершить программу";
  }
}
