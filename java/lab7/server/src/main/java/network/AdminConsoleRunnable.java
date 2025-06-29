package network;

import java.util.Scanner;
import logging.ServerLogger;
import storage.CityManager;

/**
 * Потоковая админ-консоль для управления сервером. Поддерживает команды: switch to admin, status,
 * save_internal, exit, shutdown,enable_logging,disable_logging.
 */
public class AdminConsoleRunnable implements Runnable {
  private final Scanner scanner;
  private final CityManager cityManager;
  private final String adminPassword;
  private volatile boolean running = true;
  private boolean adminMode = false;

  public AdminConsoleRunnable(Scanner scanner, CityManager cityManager, String adminPassword) {
    this.scanner = scanner;
    this.cityManager = cityManager;
    this.adminPassword = adminPassword;
  }

  @Override
  public void run() {
    while (running) {
      System.out.print(adminMode ? "[SERVER-ADMIN] > " : "> ");
      String input = scanner.nextLine().trim();

      if (!adminMode && input.equalsIgnoreCase("switch to admin")) {
        System.out.print("Введите пароль администратора: ");
        String password = scanner.nextLine().trim();
        if (password.equals(adminPassword)) {
          ServerLogger.log("[SERVER] Доступ администратора предоставлен.");
          adminMode = true;
        } else {
          ServerLogger.log("[SERVER] Неверный пароль.");
        }
        continue;
      }

      if (adminMode) {
        switch (input) {
          case "status" ->
              ServerLogger.log(
                  "[SERVER] Элементов в коллекции: " + cityManager.getCollectionSize());

          case "exit" -> {
            ServerLogger.log("[ADMIN] Выход из режима администратора.");
            adminMode = false;
          }
          case "enable_logging" -> ServerLogger.enable();

          case "disable_logging" -> ServerLogger.disable();

          case "shutdown" -> {
            System.out.print("Вы уверены, что хотите завершить сервер? (yes/no): ");
            String confirm = scanner.nextLine().trim();
            if (confirm.equalsIgnoreCase("yes")) {
              ServerLogger.log("[ADMIN] Сервер завершён по команде администратора.");
              UDPServer.shutdown();
              shutdown();
            } else {
              ServerLogger.log("[SERVER] Отмена завершения.");
            }
          }

          default -> System.out.println("[ADMIN] Неизвестная команда администратора.");
        }
      } else {
        System.out.println(
            "Неизвестная команда. Введите 'switch to admin' для доступа к функциям администратора.");
      }
    }
  }

  public void shutdown() {
    running = false;
  }
}
