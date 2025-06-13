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
  public void execute(String[] args) throws IOException {
    if (!CurrentSession.isLoggedIn()) {
      System.out.println("[CLIENT] Ошибка: Неизвестная команда: replace_if_lower");
      return;
    }
    if (args.length < 1) {
      System.out.println("[CLIENT] Укажите ID объекта для обновления.");
      return;
    }

    CommandRequest checkRequest =
        new CommandRequest(
            "check-ownership",
            new String[] {args[0]},
            null,
            CurrentSession.getUsername(),
            CurrentSession.getPassword());
    CommandResponse checkResponse = sender.send(checkRequest);
    if (!checkResponse.getMessage().toLowerCase().contains("ok")) {
      System.out.println("[CLIENT] " + checkResponse.getMessage());
      return;
    }

    CityInputStrategy strategy = new CityInputStrategy(reader);
    City city = strategy.inputObject();

    CommandRequest updateRequest =
        new CommandRequest(
            "update",
            new String[] {args[0]},
            city,
            CurrentSession.getUsername(),
            CurrentSession.getPassword());
    CommandResponse response = sender.send(updateRequest);
  }

  @Override
  public String getName() {
    return "update";
  }
}
