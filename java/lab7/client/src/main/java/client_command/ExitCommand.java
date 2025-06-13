package client_command;

import input.InputLoopHandler;
import java.io.IOException;

public class ExitCommand implements ClientCommand {
  private final InputLoopHandler loop;

  public ExitCommand(InputLoopHandler loop) {
    this.loop = loop;
  }

  @Override
  public void execute(String[] args) throws IOException {
    System.out.println("Завершение работы клиента...");
    loop.stop();
  }

  @Override
  public String getName() {
    return "exit";
  }
}
