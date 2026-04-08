package lab6.server;

import lab6.common.NetworkMessage;
import lab6.common.commands.Command;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler {
    private static final Logger logger = Logger.getLogger(RequestHandler.class.getName());

    private final SocketChannel clientChannel;
    private final ServerManager manager;

    // Хранилище состояния для каждого клиента
    private final Map<SocketChannel, ByteBuffer> readBuffers = new HashMap<>();
    private final Map<SocketChannel, ByteArrayOutputStream> readStreams = new HashMap<>();

    public RequestHandler(SocketChannel channel, ServerManager manager) {
        this.clientChannel = channel;
        this.manager = manager;

        ByteBuffer buffer = ByteBuffer.allocate(8192);
        readBuffers.put(channel, buffer);
        readStreams.put(channel, new ByteArrayOutputStream());
    }

    public void handleRead() throws IOException {
        ByteBuffer buffer = readBuffers.get(clientChannel);
        ByteArrayOutputStream stream = readStreams.get(clientChannel);

        int bytesRead = clientChannel.read(buffer);

        if (bytesRead == -1) {
            closeChannel();
            return;
        }

        buffer.flip();

        // Копируем данные из буфера в поток
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        stream.write(data);

        buffer.clear();

        try {
            // Пытаемся десериализовать объект из накопленных данных
            ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(stream.toByteArray()));
            NetworkMessage requestMsg = (NetworkMessage) objectIn.readObject();

            stream.reset();
            stream.close();
            readStreams.put(clientChannel, new ByteArrayOutputStream());

            if (requestMsg.getType() == NetworkMessage.MessageType.REQUEST) {
                Command command = (Command) requestMsg.getData();
                String result = manager.handleCommand(command);

                sendResponse(result);
            } else {
                logger.warning("Получено неизвестное сообщение");
            }
        } catch (EOFException e) {
            // Недостаточно данных для полной десериализации
            // Ждем следующих данных
            logger.finest("Недостаточно данных для десериализации");
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Не найден класс при десериализации!", e);
            sendError("Ошибка формата данных.");
            closeChannel();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка обработки запроса", e);
            sendError("Ошибка выполнения команды: " + e.getMessage());
            closeChannel();
        }
    }

    private void sendResponse(String message) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOut = new ObjectOutputStream(outputStream);
        objectOut.writeObject(new NetworkMessage(message, NetworkMessage.MessageType.RESPONSE));
        objectOut.flush();

        ByteBuffer responseBuffer = ByteBuffer.wrap(outputStream.toByteArray());
        clientChannel.write(responseBuffer);
    }

    private void sendError(String message) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOut = new ObjectOutputStream(outputStream);
        objectOut.writeObject(new NetworkMessage(message, NetworkMessage.MessageType.ERROR));
        objectOut.flush();

        ByteBuffer errorBuffer = ByteBuffer.wrap(outputStream.toByteArray());
        clientChannel.write(errorBuffer);
    }

    public void closeChannel() {
        try {
            clientChannel.close();
            readBuffers.remove(clientChannel);
            readStreams.remove(clientChannel);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Ошибка закрытия канала", e);
        }
    }
}