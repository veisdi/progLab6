package lab6.client;

import java.util.logging.*;

public class ClientApp {
    public static void main(String[] args) {
        // Настройка логгера
        Logger logger = Logger.getLogger(ClientApp.class.getName());
        try {
            FileHandler fh = new FileHandler("client.log");
            logger.addHandler(fh);
        } catch (Exception e) { e.printStackTrace(); }

        String host = "localhost";
        int port = 2424;

        if (args.length >= 1) host = args[0];
        if (args.length >= 2) port = Integer.parseInt(args[1]);

        try {
            new ClientManager(host, port).run();
        } catch (Exception e) {
            System.err.println("Не удалось подключиться к серверу: " + e.getMessage());
        }
    }
}