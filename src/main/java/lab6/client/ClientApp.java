package lab6.client;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class ClientApp {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 8085;

        if (args.length >= 2) {
            host = args[0];
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Порт должен быть числом!");
                return;
            }
        } else if (args.length == 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Порт должен быть числом!");
                return;
            }
        }

        System.out.println("Попытка подключения к серверу " + host + ":" + port + "...");

        try {
            SocketChannel channel = SocketChannel.open();
            channel.connect(new InetSocketAddress(host, port));

            System.out.println("Подключение успешно установлено!");

            new ClientManager(channel).run();

        } catch (Exception e) {
            System.err.println("Не удалось подключиться к серверу: " + e.getMessage());
            }
    }
}