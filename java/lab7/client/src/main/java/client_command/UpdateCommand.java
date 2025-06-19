package client_command;

import data.DataReader;
import input_object.CityInputStrategy;
import java.io.IOException;
import java.util.Scanner;
import model.City;
import network.CommandRequest;
import network.CommandResponse;
import network.CommandSender;
import session.CurrentSession;

public class UpdateCommand implements ClientCommand {
  private final CommandSender sender;
  private final DataReader<City> reader;
  private final Scanner scanner;

  public UpdateCommand(CommandSender sender, DataReader<City> reader, Scanner scanner) {
    this.sender = sender;
    this.reader = reader;
    this.scanner = scanner;
  }

  @Override
  public void execute(String[] args, DataReader<City> reader) throws IOException {
    if (!CurrentSession.isLoggedIn()) {
      System.out.println("[CLIENT] Ошибка: Неизвестная команда: update");
      return;
    }
    if (args.length < 1) {
      System.out.println("[CLIENT] Укажите ID объекта для обновления.");
      return;
    }
    String idArg = args[0];

    CommandRequest checkRequest =
        new CommandRequest(
            "check_ownership",
            new String[] {idArg},
            (City) null,
            CurrentSession.getToken().orElse(null));

    CommandResponse checkResponse = sender.send(checkRequest);

    if (!checkResponse.getMessage().equals("OK")) {
      return;
    }

    CityInputStrategy strategy = new CityInputStrategy(reader);
    City city = strategy.inputObject();

    CommandRequest updateRequest =
        new CommandRequest(
            getName(), new String[] {args[0]}, city, CurrentSession.getToken().orElse(null));
    CommandResponse response = sender.send(updateRequest);
  }

  @Override
  public String getName() {
    return "update";
  }
}
