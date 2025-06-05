package main;

import client_command.ClientCommandProcessor;
import data.ConsoleDataReader;
import data.DataReader;
import input.InputLoopHandler;
import model.City;
import network.CommandSender;
import util.EnvReader;

import java.io.IOException;
import java.util.Scanner;

public class UDPClient {
  private static final int SERVER_PORT = EnvReader.getPort("SERVER_PORT", 1488);
  private static final String SERVER_HOST = "localhost";

  public static void main(String[] args) {
    try {
      Scanner scanner = new Scanner(System.in);
      CommandSender sender = new CommandSender(SERVER_HOST, SERVER_PORT);
      DataReader<City> reader = new ConsoleDataReader<>(scanner);

      ClientCommandProcessor processor = new ClientCommandProcessor(sender, scanner, reader);
      InputLoopHandler loop = new InputLoopHandler(scanner, processor);

      loop.start();

    } catch (IOException e) {
      System.out.println("[CLIENT] Ошибка клиента: " + e.getMessage());
    }
  }
}