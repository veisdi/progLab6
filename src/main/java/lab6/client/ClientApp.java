package lab6.client;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class ClientApp {
    public static void main(String[] args) {
        String host = "localhost"; // Или localhost для тестов
        int port = 8085; // Твой порт

        // Если пользователь передал аргументы при запуске, используем их
        if (args.length >= 2) {
            host = args[0];
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Порт должен быть числом!");
                return;
            }
        } else if (args.length == 1) {
            // Если передан только порт
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Порт должен быть числом!");
                return;
            }
        }

        System.out.println("Попытка подключения к серверу " + host + ":" + port + "...");

        try {
            // 1. Создаем канал и подключаемся
            SocketChannel channel = SocketChannel.open();
            channel.connect(new InetSocketAddress(host, port));

            System.out.println("Подключение успешно установлено!");

            // 2. Передаем ГОТОВЫЙ канал в менеджер (ОДИН аргумент!)
            new ClientManager(channel).run();

        } catch (Exception e) {
            System.err.println("Не удалось подключиться к серверу: " + e.getMessage());
            // e.printStackTrace(); // Раскомментируй, если нужно видеть полную ошибку
        }
    }
}