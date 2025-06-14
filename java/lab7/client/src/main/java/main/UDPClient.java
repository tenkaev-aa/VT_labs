package main;

import client_command.ClientCommandProcessor;
import data.ConsoleDataReader;
import data.DataReader;
import input.InputLoopHandler;
import java.io.IOException;
import java.util.Scanner;
import model.City;
import network.CommandSender;
import util.EnvReader;

public class UDPClient {
  private static final int SERVER_PORT = EnvReader.getPort("SERVER_PORT", 1488);
  private static final String SERVER_HOST = "localhost";

  public static void main(String[] args) {
    try {
      Scanner scanner = new Scanner(System.in);
      CommandSender sender = new CommandSender(SERVER_HOST, SERVER_PORT);
      DataReader<City> reader = new ConsoleDataReader<>(scanner);

      InputLoopHandler loop = new InputLoopHandler(scanner);
      ClientCommandProcessor processor = new ClientCommandProcessor(sender, scanner, reader, loop);
      loop.bindProcessor(processor);
      loop.start();

    } catch (IOException e) {
      System.out.println("[CLIENT] Ошибка клиента: " + e.getMessage());
    }
  }
}
