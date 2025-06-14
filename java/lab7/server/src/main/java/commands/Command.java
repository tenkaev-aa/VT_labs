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

  default boolean isAuthorizedOnly() {
    return false;
  }

  /** Доступна только неавторизованным пользователям. Например: login, register. */
  default boolean isInternalOnly() {
    return false;
  }

  /**
   * @return краткое описание команды
   */
  String getDescription();
}
