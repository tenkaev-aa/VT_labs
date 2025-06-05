package network;

import auth.PasswordHasher;
import database.dao.CityDAO;
import database.dao.UserDAO;
import database.daoimpl.PostgreUserDAO;
import database.daoimpl.PostgresCityDAO;
import model.City;
import model.CityComparator;
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
import java.util.Collection;
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
    UserDAO userDAO=new PostgreUserDAO();
    CityDAO cityDAO=new PostgresCityDAO();
    dataProcessor.processData(cityDataList);
    IdGenerator.init(cityManager.getAllIds());
    CommandRegistry.register("help", new HelpCommand());
    CommandRegistry.register("clear", new ClearCommand(cityManager));
    CommandRegistry.register(
        "filter_starts_with_name", new FilterStartsWithNameCommand(cityManager));
    CommandRegistry.register("group_counting_by_id", new GroupCountingByIdCommand(cityManager));
    CommandRegistry.register("info", new InfoCommand(cityManager));
    CommandRegistry.register("insert", new InsertCommand(cityManager,cityDAO,userDAO));
    CommandRegistry.register("print_ascending", new PrintAscendingCommand(cityManager));
    CommandRegistry.register(
        "remove_greater", new RemoveGreaterCommand(cityManager, cityComparator));
    CommandRegistry.register("remove_greater_key", new RemoveGreaterKeyCommand(cityManager));
    CommandRegistry.register("remove_key", new RemoveKeyCommand(cityManager,cityDAO,userDAO));
    CommandRegistry.register(
        "replace_if_lowe", new ReplaceIfLoweCommand(cityManager, cityComparator));
    SaveCommand saveCommand = new SaveCommand(cityManager, envFilename);
    CommandRegistry.register("show", new ShowCommand(cityManager));
    CommandRegistry.register("update", new UpdateCommand(cityManager,cityDAO,userDAO));
    Runtime.getRuntime().addShutdownHook(new Thread(saveCommand::execute));

    ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
    //Collection<City> loaded = cityDAO.findAll();
    //cityManager.loadAllFromDB(loaded);
    //System.out.println("[SERVER] Загружено из БД: " + loaded.size() + " объектов.");
    try (DatagramChannel channel = DatagramChannel.open()) {
      channel.configureBlocking(false);
      channel.bind(new InetSocketAddress(port));

      ServerLogger.log("[SERVER] Сервер запущен на порту " + port);

      AdminConsoleRunnable console =
          new AdminConsoleRunnable(new Scanner(System.in), cityManager, saveCommand, "123");
      Thread adminThread = new Thread(console);
      adminThread.start();

      while (isRunning) {
        buffer.clear();

        SocketAddress clientAddr = channel.receive(buffer);

        if (clientAddr != null) {
          buffer.flip();
          ObjectInputStream ois =
              new ObjectInputStream(new ByteArrayInputStream(buffer.array(), 0, buffer.limit()));
          CommandRequest request = (CommandRequest) ois.readObject();

          ServerLogger.log(
              "[REQUEST] От " + clientAddr + " → команда: " + request.getCommandName());

          Command command = CommandRegistry.get(request.getCommandName());

          CommandResponse response;
          if (command == null) {
            response = new CommandResponse("Неизвестная команда: " + request.getCommandName());
          } else {
            String cmdName = request.getCommandName().trim().toLowerCase();

            if (!cmdName.equals("register") && !cmdName.equals("login")) {
              String username = request.getUsername();
              String password = request.getPassword();

              if (username == null || password == null) {
                response = new CommandResponse("Ошибка авторизации: логин и пароль обязательны.");
              } else {
                String hash = PasswordHasher.hash(password);
                if (!userDAO.checkCredentials(username, hash)) {
                  response = new CommandResponse("Ошибка авторизации: неверный логин или пароль.");
                } else {
                  response = command.execute(request);
                }
              }
            } else {
              // register/login
              response = command.execute(request);
            }
          };

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
