package lab6.client;

import lab6.common.NetworkMessage;
import lab6.common.commands.*;
import lab6.common.models.SpaceMarine;

import java.io.*;
import java.nio.channels.SocketChannel;
import java.nio.channels.Channels;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientManager {
    private static final Logger logger = Logger.getLogger(ClientManager.class.getName());

    private final SocketChannel channel;
    private final Scanner scanner; // Для чтения ввода пользователя

    public ClientManager(SocketChannel channel) {
        this.channel = channel;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Главный цикл работы клиента: чтение команд и отправка на сервер.
     */
    public void run() {
        System.out.println("Добро пожаловать! Введите команду (help для справки):");

        try {
            while (true) {
                System.out.print("> ");
                String inputLine = scanner.nextLine().trim();

                if (inputLine.isEmpty()) {
                    continue;
                }

                // Используем StringTokenizer для корректного разделения команды и аргумента
                StringTokenizer st = new StringTokenizer(inputLine);
                if (!st.hasMoreTokens()) continue;

                String commandName = st.nextToken();
                String argument = st.hasMoreTokens() ? st.nextToken() : null;

                // Обработка команды execute_script отдельно, до создания объекта команды
                if (commandName.equalsIgnoreCase("execute_script")) {
                    if (argument == null) {
                        System.err.println("Ошибка: укажите имя файла скрипта!");
                    } else {
                        processScriptFile(argument);
                    }
                    continue; // Переходим к следующей итерации цикла
                }

                // Создание объекта команды
                Command command = createCommand(commandName, argument);

                if (command != null) {
                    sendCommand(command);

                    // Если команда exit, завершаем работу клиента
                    if (command instanceof ExitCommand) {
                        System.out.println("Программа завершена.");
                        break;
                    }
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка соединения с сервером", e);
            System.err.println("Соединение с сервером разорвано.");
        } finally {
            try {
                channel.close();
                scanner.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Отправляет объект команды на сервер и получает ответ.
     */
    private void sendCommand(Command command) throws IOException {
        // Формируем сообщение запроса
        NetworkMessage request = new NetworkMessage(command, NetworkMessage.MessageType.REQUEST);

        // Сериализуем и отправляем
        OutputStream outputStream = Channels.newOutputStream(channel);
        ObjectOutputStream objectOut = new ObjectOutputStream(outputStream);
        objectOut.writeObject(request);
        objectOut.flush();

        // Читаем ответ
        InputStream inputStream = Channels.newInputStream(channel);
        ObjectInputStream objectIn = new ObjectInputStream(inputStream);

        try {
            NetworkMessage response = (NetworkMessage) objectIn.readObject();

            if (response.getType() == NetworkMessage.MessageType.RESPONSE) {
                System.out.println(response.getData());
            } else if (response.getType() == NetworkMessage.MessageType.ERROR) {
                System.err.println("Ошибка сервера: " + response.getData());
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Ошибка формата ответа от сервера.");
        }
    }

    /**
     * Фабрика команд: создает нужный объект команды в зависимости от ввода.
     */
    private Command createCommand(String name, String arg) {
        try {
            switch (name.toLowerCase()) {
                case "help":
                    return new HelpCommand();

                case "info":
                    return new InfoCommand();

                case "show":
                    return new ShowCommand();

                case "add":
                    System.out.println("Ввод данных для добавления нового элемента:");
                    SpaceMarine marine = InputHelper.readMarine(scanner);
                    if (marine != null) {
                        return new AddCommand(marine);
                    } else {
                        System.err.println("Не удалось создать морпеха. Команда отменена.");
                        return null;
                    }

                case "update_id":
                    System.out.println("Ввод данных для обновления элемента:");
                    SpaceMarine updatedMarine = InputHelper.readMarine(scanner);
                    if (updatedMarine != null && updatedMarine.getId() != null) {
                        return new UpdateIdCommand(updatedMarine);
                    } else {
                        System.err.println("Некорректные данные для обновления (нужен ID).");
                        return null;
                    }

                case "remove_by_id":
                    if (arg == null) {
                        System.err.println("Укажите ID для удаления!");
                        return null;
                    }
                    long removeId = Long.parseLong(arg);
                    return new RemoveByIdCommand(removeId);

                case "clear":
                    return new ClearCommand();

                case "group_counting_by_creation_date":
                    return new GroupCountingByCreationDateCommand();

                case "filter_starts_with_achievements":
                    if (arg == null) {
                        System.err.println("Укажите префикс для фильтрации!");
                        return null;
                    }
                    return new FilterStartsWithAchievementsCommand(arg);

                case "exit":
                    return new ExitCommand();

                default:
                    System.err.println("Неизвестная команда: " + name + ". Введите 'help' для справки.");
                    return null;
            }
        } catch (NumberFormatException e) {
            System.err.println("Ошибка формата числа: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Ошибка при создании команды: " + e.getMessage());
            return null;
        }
    }

    /**
     * Читает файл со скриптом и выполняет команды построчно.
     */
    private void processScriptFile(String fileName) {
        File scriptFile = new File(fileName);

        if (!scriptFile.exists()) {
            System.err.println("Файл скрипта не найден: " + fileName);
            return;
        }

        System.out.println("=== Выполнение скрипта из файла: " + fileName + " ===");

        try (Scanner fileScanner = new Scanner(scriptFile)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();

                // Пропускаем пустые строки и комментарии
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                System.out.println("> " + line); // Эхо команды

                StringTokenizer st = new StringTokenizer(line);
                if (!st.hasMoreTokens()) continue;

                String cmdName = st.nextToken();
                String cmdArg = st.hasMoreTokens() ? st.nextToken() : null;

                // Рекурсивный вызов для вложенных скриптов
                if (cmdName.equalsIgnoreCase("execute_script")) {
                    if (cmdArg != null) {
                        processScriptFile(cmdArg);
                    } else {
                        System.err.println("Ошибка: укажите имя файла скрипта внутри скрипта!");
                    }
                    continue;
                }

                Command command = createCommand(cmdName, cmdArg);
                if (command != null) {
                    sendCommand(command);

                    // Если в скрипте встретился exit, прерываем выполнение скрипта
                    if (command instanceof ExitCommand) {
                        System.out.println("Выход из программы по команде в скрипте.");
                        return;
                    }
                }
            }
            System.out.println("=== Скрипт завершен ===");

        } catch (Exception e) {
            System.err.println("Ошибка при выполнении скрипта: " + e.getMessage());
        }
    }
}