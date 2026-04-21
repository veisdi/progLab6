package lab6.server;

import lab6.common.models.*;
import lab6.server.commands.*;

import java.io.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.logging.*;
import java.util.stream.Collectors;

public class ServerManager {
    private static final Logger logger = Logger.getLogger(ServerManager.class.getName());
    private Vector<SpaceMarine> collection;
    private String fileName;

    /**
     * Загружает коллекцию из CSV файла.
     * @param fileName Путь к файлу.
     * @return true, если загрузка прошла успешно (или файл был создан).
     */
    public ServerManager(String fileName) {
        this.fileName = fileName; // Сохраняем путь из аргумента (например, "resources/marines.csv")
        this.collection = new Vector<>();
        loadFromFile(); // Вызываем метод без аргументов, он сам возьмет this.fileName
    }

    /**
     * Загружает коллекцию из файла, путь к которому хранится в this.fileName
     */
    public boolean loadFromFile() {
        // Используем поле класса, которое мы сохранили в конструкторе
        String path = this.fileName;

        logger.info("Попытка загрузки коллекции из файла: " + path);
        File file = new File(path);

        // Если файла нет, создаем пустую коллекцию и сбрасываем ID на 1
        if (!file.exists()) {
            logger.warning("Файл не найден: " + path + ". Будет создана новая коллекция.");
            collection.clear();
            SpaceMarine.setNextId(1);
            return true;
        }

        if (!file.canRead()) {
            logger.severe("Нет прав на чтение файла: " + path);
            return false;
        }

        try (Scanner scanner = new Scanner(file)) {
            collection.clear();

            long maxIdInFile = 0;
            int linesProcessed = 0;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                if (line.startsWith("id;")) continue;

                try {
                    SpaceMarine marine = parseLine(line);
                    if (marine != null) {
                        collection.add(marine);
                        if (marine.getId() > maxIdInFile) {
                            maxIdInFile = marine.getId();
                        }
                        linesProcessed++;
                    }
                } catch (Exception e) {
                    logger.warning("Ошибка при парсинге строки: " + line);
                }
            }

            if (!collection.isEmpty()) {
                SpaceMarine.setNextId(maxIdInFile + 1);
                logger.info("Успешно загружено " + linesProcessed + " объектов.");
            } else {
                SpaceMarine.setNextId(1);
                logger.info("Коллекция пуста после загрузки.");
            }
            return true;

        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Ошибка чтения файла", e);
            return false;
        }
    }

    /**
     * Вспомогательный метод для парсинга одной строки CSV в объект SpaceMarine.
     */
    /**
     * Парсит одну строку CSV в объект SpaceMarine.
     */
    private SpaceMarine parseLine(String line) {
        String[] parts = line.split(";");

        // Проверяем минимальное количество полей (должно быть至少 9)
        if (parts.length < 9) {
            throw new IllegalArgumentException("Недостаточно полей в строке: " + line);
        }

        try {
            long id = Long.parseLong(parts[0].trim());


            String name = parts[1].trim();

            String coordsStr = parts[2].trim();
            // Удаляем скобки
            coordsStr = coordsStr.replace("(", "").replace(")", "");

            // Разделяем по двоеточию или запятой
            String[] coordParts = coordsStr.split("[:;,]");
            if (coordParts.length != 2) {
                throw new IllegalArgumentException("Неверный формат координат: " + coordsStr);
            }

            int x = Integer.parseInt(coordParts[0].trim());
            double y = Double.parseDouble(coordParts[1].trim().replace(',', '.')); // Заменяем запятую на точку!

            Coordinates coordinates = new Coordinates(x, y);


            String dateStr = parts[3].trim();
            ZonedDateTime creationDate = ZonedDateTime.parse(dateStr);


            long health = Long.parseLong(parts[4].trim());

            boolean loyal = Boolean.parseBoolean(parts[5].trim());


            String achievements = parts[6].trim();
            if (achievements.isEmpty()) achievements = null;


            MeleeWeapon weapon = null;
            String weaponStr = parts[7].trim();
            if (!weaponStr.isEmpty()) {
                try {
                    weapon = MeleeWeapon.valueOf(weaponStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    logger.warning("Неизвестное оружие: " + weaponStr + ". Будет установлено null.");
                }
            }


            String chapterName = parts[8].trim();
            String chapterWorld = "";

            if (parts.length > 9 && !parts[9].trim().isEmpty()) {
                chapterWorld = parts[9].trim();
            } else {

                chapterWorld = "";
            }

            Chapter chapter = new Chapter(chapterName, chapterWorld);


            return new SpaceMarine(id, name, coordinates, creationDate, health, loyal, achievements, weapon, chapter);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ошибка преобразования числа в строке: " + line, e);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Ошибка формата даты в строке: " + line, e);
        }
    }
    public void saveToFile() {
        if (fileName == null || fileName.isEmpty()) {
            logger.warning("Имя файла не указано, сохранение невозможно.");
            return;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {

            for (SpaceMarine marine : collection) {
                StringBuilder line = new StringBuilder();
                line.append(marine.getId()).append(";");
                line.append(marine.getName()).append(";");
                line.append("(").append(marine.getCoordinates().getX()).append(":").append(marine.getCoordinates().getY()).append(");");
                line.append(marine.getCreationDate()).append(";");
                line.append(marine.getHealth()).append(";");
                line.append(marine.getLoyal()).append(";");
                line.append(marine.getAchievements() != null ? marine.getAchievements() : "").append(";");
                line.append(marine.getMeleeWeapon() != null ? marine.getMeleeWeapon() : "").append(";");

                if (marine.getChapter() != null) {
                    line.append(marine.getChapter().getName());
                    if (marine.getChapter().getWorld() != null) {
                        line.append(";").append(marine.getChapter().getWorld());
                    }
                }

                writer.println(line.toString());
            }
            logger.info("Коллекция успешно сохранена в файл: " + fileName);

        } catch (IOException e) {
            logger.severe("Ошибка при сохранении файла: " + e.getMessage());
        }
    }

    // Методы управления коллекцией
    public void addMarine(SpaceMarine marine) {
        collection.add(marine);
        logger.info("Добавлен элемент с ID: " + marine.getId());
    }

    public String show() {
        // Сортировка по имени перед отправкой
        return collection.stream()
                .sorted(Comparator.comparing(SpaceMarine::getName))
                .map(SpaceMarine::toString)
                .collect(Collectors.joining("\n"));
    }

    public String info() {
        return "Тип: " + collection.getClass().getName() + "\nРазмер: " + collection.size();
    }

    /**
     * Возвращает информацию о коллекции: тип, дата инициализации, количество элементов.
     */
    public String getInfo() {
        StringBuilder info = new StringBuilder();


        info.append("Тип коллекции: ").append(collection.getClass().getName()).append("\n");


        if (collection.isEmpty()) {
            info.append("Дата инициализации: Коллекция пуста\n");
        } else {

            ZonedDateTime initDate = collection.stream()
                    .min(Comparator.comparing(SpaceMarine::getCreationDate))
                    .map(SpaceMarine::getCreationDate)
                    .orElse(null);

            if (initDate != null) {
                info.append("Дата инициализации: ").append(initDate).append("\n");
            } else {
                info.append("Дата инициализации: Неизвестно\n");
            }
        }


        info.append("Количество элементов: ").append(collection.size());

        return info.toString();
    }
    // Обработчик входящей команды
    public String handleCommand(Command command) {
        logger.info("Получена команда: " + command.getClass().getSimpleName());
        try {
            return command.execute(this);
        } catch (Exception e) {
            logger.severe("Ошибка выполнения команды: " + e.getMessage());
            return "Ошибка: " + e.getMessage();
        }
    }

    // Метод для обновления элемента
    public boolean updateMarine(SpaceMarine marine) {
        for (int i = 0; i < collection.size(); i++) {
            if (collection.get(i).getId().equals(marine.getId())) {
                collection.set(i, marine);
                logger.info("Обновлен элемент с ID: " + marine.getId());
                return true;
            }
        }
        return false;
    }

    // Метод для удаления по ID
    public boolean removeById(long id) {
        boolean removed = collection.removeIf(marine -> marine.getId() == id);
        if (removed) {
            logger.info("Удален элемент с ID: " + id);
        }
        return removed;
    }


    public void clearCollection() {
        collection.clear();
        logger.info("Коллекция очищена");
    }

    public String groupCountingByCreationDate() {
        return collection.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        SpaceMarine::getCreationDate,
                        java.util.stream.Collectors.counting()
                ))
                .entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(java.util.stream.Collectors.joining("\n"));
    }


    public String filterStartsWithAchievements(String prefix) {
        return collection.stream()
                .filter(marine -> marine.getAchievements() != null &&
                        marine.getAchievements().startsWith(prefix))
                .map(SpaceMarine::toString)
                .collect(java.util.stream.Collectors.joining("\n"));
    }
}