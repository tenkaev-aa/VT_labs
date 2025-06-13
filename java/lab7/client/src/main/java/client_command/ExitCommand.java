package client_command;

import data.DataReader;
import input.InputLoopHandler;
import model.City;

public class ExitCommand implements ClientCommand {
  private final InputLoopHandler loop;

  public ExitCommand(InputLoopHandler loop) {
    this.loop = loop;
  }

  @Override
  public void execute(String[] args, DataReader<City> reader) {
    System.out.println("Завершение работы клиента...");
    loop.stop();
  }

  @Override
  public String getName() {
    return "exit";
  }
}
