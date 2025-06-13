package client_command;

import java.io.IOException;

public interface ClientCommand {
  void execute(String[] args) throws IOException;

  String getName();
}
