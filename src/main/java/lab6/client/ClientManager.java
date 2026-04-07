package lab6.client;

import lab6.common.NetworkMessage;
import lab6.common.commands.*;
import lab6.common.models.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Scanner;
import java.util.logging.*;

public class ClientManager {
    private static final Logger logger = Logger.getLogger(ClientManager.class.getName());
    private SocketChannel channel;
    private Scanner scanner;

    public ClientManager(String host, int port) throws IOException {
        scanner = new Scanner(System.in);
        channel = SocketChannel.open();
        channel.connect(new InetSocketAddress(host, port));
        // По умолчанию канал блокирующий, что удобно для клиента (ждем ответа)
        logger.info("Подключено к серверу " + host + ":" + port);
    }

    public void run() {
        System.out.println("Добро пожаловать! Введите команду (help для справки):");
        try {
            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) continue;

                String[] parts = input.split("\\s+", 2);
                String cmdName = parts[0].toLowerCase();
                String arg = parts.length > 1 ? parts[1] : "";

                Command command = createCommand(cmdName, arg);
                if (command == null) continue;

                // Отправка
                NetworkMessage request = new NetworkMessage(command, NetworkMessage.MessageType.REQUEST);
                ObjectOutputStream out = new ObjectOutputStream(Channels.newOutputStream(channel));
                out.writeObject(request);
                out.flush();

                // Получение ответа
                ObjectInputStream in = new ObjectInputStream(Channels.newInputStream(channel));
                NetworkMessage response = (NetworkMessage) in.readObject();

                if (response.getType() == NetworkMessage.MessageType.RESPONSE) {
                    System.out.println(response.getData());
                } else if (response.getType() == NetworkMessage.MessageType.ERROR) {
                    System.err.println("Ошибка сервера: " + response.getData());
                }

                if (cmdName.equals("exit")) break;
            }
        } catch (Exception e) {
            System.err.println("Ошибка связи с сервером: " + e.getMessage());
        } finally {
            try {
                channel.close();
            } catch (IOException e) {
            }
        }
    }

    // Фабрика команд (упрощенная)
    private Command createCommand(String name, String arg) {
        try {
            switch (name.toLowerCase()) {
                case "help":
                    return new HelpCommand();

                case "add":
                    System.out.println("Ввод данных для добавления...");
                    SpaceMarine marine = InputHelper.readMarine(scanner);
                    if (marine != null) {
                        return new AddCommand(marine);
                    } else {
                        System.err.println("Не удалось создать морпеха. Команда отменена.");
                        return null;
                    }

                case "update_id":
                    System.out.println("Ввод данных для обновления...");
                    SpaceMarine updatedMarine = InputHelper.readMarine(scanner);
                    if (updatedMarine != null && updatedMarine.getId() != null) {
                        return new UpdateIdCommand(updatedMarine);
                    } else {
                        System.err.println("Некорректные данные для обновления.");
                        return null;
                    }

                case "remove_by_id":
                    if (arg == null || arg.trim().isEmpty()) {
                        System.err.println("Укажите ID для удаления!");
                        return null;
                    }
                    long removeId = Long.parseLong(arg.trim());
                    return new RemoveByIdCommand(removeId);

                case "clear":
                    return new ClearCommand();

                case "group_counting_by_creation_date":
                    return new GroupCountingByCreationDateCommand();

                case "filter_starts_with_achievements":
                    if (arg == null || arg.trim().isEmpty()) {
                        System.err.println("Укажите префикс для фильтрации!");
                        return null;
                    }
                    return new FilterStartsWithAchievementsCommand(arg.trim());

                case "show":
                    return new ShowCommand();

                case "info":
                    return new InfoCommand();

                case "exit":
                    return new ExitCommand();

                default:
                    System.err.println("Неизвестная команда: " + name);
                    return null;
            }
        } catch (Exception e) {
            System.err.println("Ошибка ввода: " + e.getMessage());
            return null;
        }
    }
}