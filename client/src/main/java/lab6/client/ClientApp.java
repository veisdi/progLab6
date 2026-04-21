package lab6.client;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Scanner;


public class ClientApp {
    // Количество попыток подключения
    private static final int MAX_ATTEMPTS = 3;
    // Задержка между попытками (в миллисекундах)
    private static final long RETRY_DELAY_MS = 2000;

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

        SocketChannel channel = null;
        boolean connected = false;

        System.out.println("Попытка подключения к серверу " + host + ":" + port + "...");

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                System.out.println("Идет подключение к серверу... (Попытка " + attempt + " из " + MAX_ATTEMPTS + ")");

                channel = SocketChannel.open();
                // Устанавливаем таймаут на подключение (опционально, чтобы не висело вечно)
                // channel.configureBlocking(true); // По умолчанию true, но можно явно указать

                // Пытаемся подключиться
                channel.connect(new InetSocketAddress(host, port));

                connected = true;
                System.out.println("Подключение успешно установлено!");
                break;

            } catch (Exception e) {
                System.err.println("Не удалось подключиться к серверу: " + e.getMessage());

                // Закрываем канал, если он успел создаться
                if (channel != null) {
                    try { channel.close(); } catch (Exception ignored) {}
                }

                if (attempt < MAX_ATTEMPTS) {
                    System.out.println("Ждем " + (RETRY_DELAY_MS / 1000) + " секунды перед следующей попыткой...");
                    try {
                        Thread.sleep(RETRY_DELAY_MS); // Пауза между попытками
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        System.err.println("Процесс прерван.");
                        return;
                    }
                }
            }
        }

        if (!connected) {
            System.err.println("Не удалось подключиться к серверу после " + MAX_ATTEMPTS + " попыток. Проверьте, запущен ли сервер и правильность адреса/порта.");
            return;
        }

        try {
            new ClientManager(channel).run();
        } catch (Exception e) {
            System.err.println("Ошибка в работе клиента: " + e.getMessage());
        } finally {
            if (channel != null && channel.isOpen()) {
                try { channel.close(); } catch (Exception ignored) {}
            }
        }
    }
}