package network;

import auth.AuthUtil;
import commands.*;
import database.dao.CityDAO;
import database.dao.UserDAO;
import database.daoimpl.HibernateCityDAO;
import database.daoimpl.HibernateUserDAO;
import exceptions.FileReadException;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Collection;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import logging.ServerLogger;
import model.City;
import model.CityComparator;
import storage.CityManager;
import util.EnvReader;

public class UDPServer {
  private static final int PORT = EnvReader.getPort("SERVER_PORT", 1488);
  private static final int BUFFER_SIZE = 65535;
  private static volatile boolean isRunning = true;

  private static final ExecutorService readPool = Executors.newFixedThreadPool(4);
  private static final ExecutorService writePool = Executors.newFixedThreadPool(4);

  public static void main(String[] args) throws FileReadException {
    CityManager cityManager = new CityManager();
    CityComparator cityComparator = new CityComparator();
    // UserDAO userDAO = new PostgreUserDAO();
    // CityDAO cityDAO = new PostgreCityDAO();
    UserDAO userDAO = new HibernateUserDAO();
    CityDAO cityDAO = new HibernateCityDAO();
    Collection<City> loadedCities = cityDAO.findAll();
    cityManager.loadAllFromDB(loadedCities);
    CommandManager commandManager =
        new CommandManager(cityManager, cityDAO, userDAO, cityComparator);

    try (DatagramChannel channel = DatagramChannel.open()) {
      channel.configureBlocking(false);
      channel.bind(new InetSocketAddress(PORT));
      ServerLogger.log("[SERVER] Сервер запущен на порту " + PORT);

      Thread adminThread =
          new Thread(new AdminConsoleRunnable(new Scanner(System.in), cityManager, "123"));
      adminThread.start();

      while (isRunning) {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        SocketAddress clientAddr = channel.receive(buffer);

        if (clientAddr != null) {
          readPool.submit(
              () -> handleRequest(buffer, clientAddr, channel, commandManager, userDAO));
        }

        Thread.sleep(5);
      }

      ServerLogger.log("[SERVER] Завершение сервера...");
    } catch (IOException | InterruptedException e) {
      ServerLogger.log("[ERROR] " + e.getMessage());
    } finally {
      readPool.shutdownNow();
      writePool.shutdownNow();
    }
  }

  public static void shutdown() {
    isRunning = false;
  }

  private static void handleRequest(
      ByteBuffer buffer,
      SocketAddress clientAddr,
      DatagramChannel channel,
      CommandManager commandManager,
      UserDAO userDAO) {
    try {
      buffer.flip();
      ObjectInputStream ois =
          new ObjectInputStream(new ByteArrayInputStream(buffer.array(), 0, buffer.limit()));
      CommandRequest request = (CommandRequest) ois.readObject();

      ServerLogger.log("[REQUEST] От " + clientAddr + " → команда: " + request.getCommandName());

      readPool.submit(
          () -> {
            CommandResponse response = processRequest(request, commandManager, userDAO);
            writePool.submit(() -> sendResponse(response, clientAddr, channel));
          });

    } catch (IOException | ClassNotFoundException e) {
      ServerLogger.log("[ERROR] Ошибка при обработке запроса: " + e.getMessage());
    }
  }

  private static CommandResponse processRequest(
      CommandRequest request, CommandManager commandManager, UserDAO userDAO) {

    String cmdName = request.getCommandName();
    Command command = commandManager.get(cmdName);

    if (command == null) {
      return new CommandResponse("Неизвестная команда: " + cmdName);
    }

    if (cmdName.equalsIgnoreCase("register")
        || cmdName.equalsIgnoreCase("login")
        || cmdName.equalsIgnoreCase("get_salt")
        || cmdName.equalsIgnoreCase("help")) {
      return command.execute(request);
    }

    String token = request.getSessionToken().orElse(null);
    if (!AuthUtil.authorizeToken(request)) {
      return new CommandResponse("Ошибка авторизации: недействительный токен.");
    }

    CommandRequest enrichedRequest =
        new CommandRequest(
            request.getCommandName(), request.getArguments(), request.getCity(), token);

    return command.execute(enrichedRequest);
  }

  private static void sendResponse(
      CommandResponse response, SocketAddress clientAddr, DatagramChannel channel) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(response);
      oos.flush();

      ByteBuffer outBuffer = ByteBuffer.wrap(baos.toByteArray());
      channel.send(outBuffer, clientAddr);

      ServerLogger.log("[RESPONSE] Ответ отправлен " + clientAddr);
    } catch (IOException e) {
      ServerLogger.log("[ERROR] Ошибка отправки ответа: " + e.getMessage());
    }
  }
}
