package network;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import model.City;
import session.CurrentSession;


public class CommandSender {
  private final DatagramChannel channel;
  private final InetSocketAddress serverAddress;
  private final int TIMEOUT_MS = 3000;
  private final int BUFFER_SIZE = 65535;

  public CommandSender(String host, int port) throws IOException {
    this.channel = DatagramChannel.open();
    this.channel.configureBlocking(false);
    this.serverAddress = new InetSocketAddress(host, port);
  }


  public void send(String commandName, String[] args, City city) {
    String username = CurrentSession.getUsername();
    String password = CurrentSession.getPassword();

    if (username == null || password == null) {
      System.out.println("[CLIENT] Вы не авторизованы. Используйте login/register.");
      return;
    }

    CommandRequest request = new CommandRequest(commandName, args, city, username, password);
    send(request); // делегируем существующему методу
  }


  public void send(String commandName, String[] args) {
    send(commandName, args, null);
  }


  public void send(String commandName) {
    send(commandName, new String[0], null);
  }


  public CommandResponse send(CommandRequest request) {
    try {
      // Сериализация
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(request);
      oos.flush();

      ByteBuffer sendBuffer = ByteBuffer.wrap(baos.toByteArray());
      channel.send(sendBuffer, serverAddress);

      // Ожидание ответа
      ByteBuffer receiveBuffer = ByteBuffer.allocate(BUFFER_SIZE);
      long start = System.currentTimeMillis();
      while (System.currentTimeMillis() - start < TIMEOUT_MS) {
        SocketAddress responseAddr = channel.receive(receiveBuffer);
        if (responseAddr != null) {
          receiveBuffer.flip();
          ByteArrayInputStream bais =
                  new ByteArrayInputStream(receiveBuffer.array(), 0, receiveBuffer.limit());
          ObjectInputStream ois = new ObjectInputStream(bais);
          CommandResponse response = (CommandResponse) ois.readObject();

          System.out.println("[CLIENT] Ответ: " + response.getMessage());

          if (response.getCities() != null && !response.getCities().isEmpty()) {
            response.getCities().forEach(System.out::println);
          }

          return response;
        }
      }
      System.out.println("[CLIENT] Сервер не ответил вовремя.");

    } catch (IOException | ClassNotFoundException e) {
      System.out.println("[CLIENT] Ошибка при передаче команды: " + e.getMessage());
    }
    return null;
  }
}

