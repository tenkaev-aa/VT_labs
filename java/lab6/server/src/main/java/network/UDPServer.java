package network;

import city.CityComparator;
import commands.*;
import data.CityData;
import data.DataHandlerFactory;
import data.DataLoader;
import data.DataProcessor;
import exceptions.FileReadException;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.Scanner;
import logging.ServerLogger;
import storage.CityManager;
import util.EnvReader;
import util.IdGenerator;

public class UDPServer {
  private static final int PORT = 1488;
  private static final int BUFFER_SIZE = 65535;

  public static void main(String[] args) throws FileReadException {
    CityComparator cityComparator = new CityComparator();
    CityManager cityManager = new CityManager();
    String envFilename = EnvReader.getFilePath();
    DataLoader loader = DataHandlerFactory.getLoader(envFilename);
    List<CityData> cityDataList = loader.loadData(envFilename);
    DataProcessor dataProcessor = new DataProcessor(cityManager);
    dataProcessor.processData(cityDataList);
    IdGenerator.init(cityManager.getAllIds());

    CommandRegistry.register("help", new HelpCommand());
    CommandRegistry.register("clear", new ClearCommand(cityManager));
    CommandRegistry.register(
        "filter_starts_with_name", new FilterStartsWithNameCommand(cityManager));
    CommandRegistry.register("group_counting_by_id", new GroupCountingByIdCommand(cityManager));
    CommandRegistry.register("info", new InfoCommand(cityManager));
    CommandRegistry.register("insert", new InsertCommand(cityManager));
    CommandRegistry.register("print_ascending", new PrintAscendingCommand(cityManager));
    CommandRegistry.register(
        "remove_greater", new RemoveGreaterCommand(cityManager, cityComparator));
    CommandRegistry.register("remove_greater_key", new RemoveGreaterKeyCommand(cityManager));
    CommandRegistry.register("remove_key", new RemoveKeyCommand(cityManager));
    CommandRegistry.register(
        "replace_if_lowe", new ReplaceIfLoweCommand(cityManager, cityComparator));
    SaveCommand saveCommand = new SaveCommand(cityManager, envFilename);
    CommandRegistry.register("show", new ShowCommand(cityManager));
    CommandRegistry.register("update", new UpdateCommand(cityManager));
    Runtime.getRuntime().addShutdownHook(new Thread(saveCommand::execute));

    Scanner scanner = new Scanner(System.in);
    String adminPassword = "123";
    final boolean[] adminMode = {false};

    new Thread(
            () -> {
              while (true) {
                System.out.print(adminMode[0] ? "[SERVER-ADMIN] > " : "> ");
                String input = scanner.nextLine().trim();

                if (!adminMode[0] && input.equalsIgnoreCase("switch to admin")) {
                  System.out.print("Введите пароль администратора: ");
                  String password = scanner.nextLine().trim();

                  if (password.equals(adminPassword)) {
                    System.out.println("[SERVER] Доступ администратора предоставлен.");
                    adminMode[0] = true;
                  } else {
                    System.out.println("[SERVER] Неверный пароль.");
                  }
                  continue;
                }

                if (adminMode[0]) {
                  switch (input) {
                    case "save_internal" -> {
                      saveCommand.execute();
                      ServerLogger.log("[ADMIN] Сохранение коллекции выполнено вручную.");
                    }

                    case "status" ->
                        ServerLogger.log(
                            "[ADMIN] Элементов в коллекции: " + cityManager.getCollectionSize());

                    case "enable_logging" -> ServerLogger.enable();

                    case "disable_logging" -> ServerLogger.disable();

                    case "exit" -> {
                      ServerLogger.log("[ADMIN] Выход из режима администратора.");
                      adminMode[0] = false;
                    }

                    case "shutdown" -> {
                      System.out.print("Вы уверены, что хотите завершить сервер? (yes/no): ");
                      String confirm = scanner.nextLine().trim();
                      if (confirm.equalsIgnoreCase("yes")) {
                        ServerLogger.log("[ADMIN] Инициирована остановка сервера.");
                        saveCommand.execute();
                        System.exit(0);
                      } else {
                        ServerLogger.log("[ADMIN] Операция завершения отменена.");
                      }
                    }

                    default ->
                        ServerLogger.log("[ADMIN] Неизвестная команда администратора: " + input);
                  }
                } else {
                  ServerLogger.log(
                      "Неизвестная команда. Введите 'switch to admin' для доступа к функциям администратора.");
                }
              }
            })
        .start();


    try (DatagramSocket socket = new DatagramSocket(PORT)) {
      ServerLogger.log("[SERVER] Сервер запущен на порту " + PORT);
      byte[] buffer = new byte[BUFFER_SIZE];

      while (true) {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        InetAddress clientAddress = packet.getAddress();
        int clientPort = packet.getPort();

        // Десериализация запроса
        ByteArrayInputStream bais =
            new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
        ObjectInputStream ois = new ObjectInputStream(bais);
        CommandRequest request = (CommandRequest) ois.readObject();
        if ("connect".equalsIgnoreCase(request.getCommandName())) {
          ServerLogger.log(
              "[CONNECT] Подключился клиент: " + clientAddress.getHostAddress() + ":" + clientPort);
          continue;
        }
        if ("exit".equalsIgnoreCase(request.getCommandName())) {
          ServerLogger.log(
              "[DISCONNECT] Клиент отключился: "
                  + clientAddress.getHostAddress()
                  + ":"
                  + clientPort);
          continue;
        }
        ServerLogger.log(
            "[REQUEST] От "
                + clientAddress.getHostAddress()
                + ":"
                + clientPort
                + " → команда: "
                + request.getCommandName());

        Command command = CommandRegistry.get(request.getCommandName());
        CommandResponse response;
        if (command != null) {
          response = command.execute(request);
        } else {
          response = new CommandResponse("Неизвестная команда: " + request.getCommandName());
        }

        // Сериализация ответа
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(response);
        oos.flush();

        byte[] responseBytes = baos.toByteArray();
        DatagramPacket responsePacket =
            new DatagramPacket(
                responseBytes, responseBytes.length, packet.getAddress(), packet.getPort());
        socket.send(responsePacket);

        ServerLogger.log("[SERVER] Ответ отправлен клиенту.");
      }
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }
}
