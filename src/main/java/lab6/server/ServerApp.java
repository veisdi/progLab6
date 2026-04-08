package lab6.server;

import lab6.common.NetworkMessage;
import lab6.common.commands.Command;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.*;

public class ServerApp {
    private static final Logger logger = Logger.getLogger(ServerApp.class.getName());
    private static final int PORT = 8085;

    public static void main(String[] args) {
        // Настройка логгера
        try {
            FileHandler fh = new FileHandler("server.log");
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (args.length < 1) {
            System.err.println("Укажите файл коллекции!");
            return;
        }

        String fileName = args[0];
        ServerManager manager = new ServerManager(fileName);
        logger.info("Сервер запущен на порту " + PORT);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Завершение работы, сохраняем коллекцию...");
            manager.saveToFile();
            logger.info("Работа сервера завершена корректно.");
        }));

        try (ServerSocketChannel serverChannel = ServerSocketChannel.open()) {
            serverChannel.socket().bind(new InetSocketAddress(PORT));
            serverChannel.configureBlocking(false);

            Selector selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if (key.isAcceptable()) {
                        SocketChannel clientChannel = serverChannel.accept();
                        if (clientChannel != null) {
                            clientChannel.configureBlocking(false);
                            RequestHandler handler = new RequestHandler(clientChannel, manager);
                            clientChannel.register(selector, SelectionKey.OP_READ, handler);
                            logger.info("Новое подключение от: " + clientChannel.getRemoteAddress());
                        }
                    } else if (key.isReadable()) {
                        RequestHandler handler = (RequestHandler) key.attachment();
                        if (handler != null) {
                            try {
                                handler.handleRead();
                            } catch (Exception e) {
                                logger.log(Level.SEVERE, "Ошибка в обработчике", e);
                                handler.closeChannel();
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.severe("Критическая ошибка сервера: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
