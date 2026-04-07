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
    private static final int PORT = 2424;

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

        try (ServerSocketChannel serverChannel = ServerSocketChannel.open()) {
            serverChannel.socket().bind(new InetSocketAddress(PORT));
            serverChannel.configureBlocking(false);

            Selector selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                selector.select(); // Ждем событий
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if (key.isAcceptable()) {
                        // Новое подключение
                        SocketChannel clientChannel = serverChannel.accept();
                        if (clientChannel != null) {
                            clientChannel.configureBlocking(false);
                            // Создаем RequestHandler и прикрепляем его к ключу
                            RequestHandler handler = new RequestHandler(clientChannel, manager);
                            clientChannel.register(selector, SelectionKey.OP_READ, handler);
                            logger.info("Новое подключение от: " + clientChannel.getRemoteAddress());
                        }
                    } else if (key.isReadable()) {
                        // Есть данные для чтения
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