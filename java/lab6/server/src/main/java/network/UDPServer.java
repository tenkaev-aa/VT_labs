package network;

import city.CityComparator;
import commands.*;
import data.CityData;
import data.DataHandlerFactory;
import data.DataLoader;
import data.DataProcessor;
import exceptions.FileReadException;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.List;
import java.util.Scanner;
import logging.ServerLogger;
import storage.CityManager;
import util.EnvReader;
import util.IdGenerator;

public class UDPServer {
  private static final int port = EnvReader.getPort("SERVER_PORT", 1488);
  private static final int BUFFER_SIZE = 65535;
  private static volatile boolean isRunning = true;

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

    ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
    try (DatagramChannel channel = DatagramChannel.open()) {
      channel.configureBlocking(false);
      channel.bind(new InetSocketAddress(port));

      ServerLogger.log("[SERVER] Сервер запущен на порту " + port);

      new Thread(new AdminConsoleRunnable(new Scanner(System.in), cityManager, saveCommand, "123")).start();

      while (isRunning) {
        buffer.clear();

        SocketAddress clientAddr = channel.receive(buffer);

        if (clientAddr != null) {
          buffer.flip();
          ObjectInputStream ois = new ObjectInputStream(
                  new ByteArrayInputStream(buffer.array(), 0, buffer.limit()));
          CommandRequest request = (CommandRequest) ois.readObject();

          ServerLogger.log("[REQUEST] От " + clientAddr + " → команда: " + request.getCommandName());

          Command command = CommandRegistry.get(request.getCommandName());
          CommandResponse response = (command != null)
                  ? command.execute(request)
                  : new CommandResponse("Неизвестная команда: " + request.getCommandName());
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          ObjectOutputStream oos = new ObjectOutputStream(baos);
          oos.writeObject(response);
          oos.flush();

          ByteBuffer outBuffer = ByteBuffer.wrap(baos.toByteArray());
          channel.send(outBuffer, clientAddr);

          ServerLogger.log("[SERVER] Ответ отправлен " + clientAddr);
        }
      }

      ServerLogger.log("[SERVER] Завершение сервера...");
    } catch (IOException | ClassNotFoundException e) {
      ServerLogger.log("[ERROR] " + e.getMessage());
    }
  }

  public static void shutdown() {
    isRunning = false;
  }

}
