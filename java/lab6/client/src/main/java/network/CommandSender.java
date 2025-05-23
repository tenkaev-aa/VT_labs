package network;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

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

  public void send(CommandRequest request) {
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

          return;
        }
      }
        System.out.println("[CLIENT] Сервер не ответил вовремя.");

    } catch (IOException | ClassNotFoundException e) {
      System.out.println("[CLIENT] Ошибка при передаче команды: " + e.getMessage());
    }
  }
}
