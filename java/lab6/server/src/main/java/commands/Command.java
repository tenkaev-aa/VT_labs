package commands;

import network.CommandRequest;
import network.CommandResponse;

public interface Command {
  /**
   * Выполняет команду на сервере.
   *
   * @param request входящий запрос
   * @return результат выполнения команды
   */
  CommandResponse execute(CommandRequest request);

  /**
   * @return краткое описание команды
   */
  String getDescription();
}
