package lab6.client;

import lab6.common.models.*;
import java.util.Scanner;

public class InputHelper {

    /**
     * Считывает данные для создания нового SpaceMarine из консоли.
     * @param scanner для чтения ввода
     * @return Готовый объект SpaceMarine
     */
    public static SpaceMarine readMarine(Scanner scanner) {
        try {
            System.out.println("=== Создание нового морпеха ===");

            String name = readValidatedString(scanner, "Введите имя: ", s -> !s.trim().isEmpty(), "Имя не может быть пустым!");
            int x = readValidatedInt(scanner, "Введите координату X (> -255): ", val -> val > -255, "X должен быть больше -255!");
            double y = readValidatedDouble(scanner, "Введите координату Y (<= 457): ", val -> val <= 457, "Y должен быть меньше или равен 457!");
            Coordinates coordinates = new Coordinates(x, y);
            long health = readValidatedLong(scanner, "Введите здоровье (> 0): ", val -> val > 0, "Здоровье должно быть положительным!");
            boolean loyal = readValidatedBoolean(scanner, "Лоялен? (true/false): ");

            System.out.print("Введите достижения (или нажмите Enter для пропуска): ");
            String achievements = scanner.nextLine().trim();
            if (achievements.isEmpty()) achievements = null;

            MeleeWeapon weapon = readWeapon(scanner);
            Chapter chapter = readChapter(scanner);

            return new SpaceMarine(name, coordinates, health, loyal, achievements, weapon, chapter);

        } catch (Exception e) {
            System.err.println("Ошибка при вводе данных: " + e.getMessage());
            return null;
        }
    }


    private static String readValidatedString(Scanner scanner, String prompt, java.util.function.Predicate<String> validator, String errorMessage) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (validator.test(input)) {
                return input;
            } else {
                System.err.println(errorMessage);
            }
        }
    }

    private static int readValidatedInt(Scanner scanner, String prompt, java.util.function.Predicate<Integer> validator, String errorMessage) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (validator.test(value)) {
                    return value;
                } else {
                    System.err.println(errorMessage);
                }
            } catch (NumberFormatException e) {
                System.err.println("Введите целое число!");
            }
        }
    }

    private static double readValidatedDouble(Scanner scanner, String prompt, java.util.function.Predicate<Double> validator, String errorMessage) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (validator.test(value)) {
                    return value;
                } else {
                    System.err.println(errorMessage);
                }
            } catch (NumberFormatException e) {
                System.err.println("Введите число!");
            }
        }
    }

    private static long readValidatedLong(Scanner scanner, String prompt, java.util.function.Predicate<Long> validator, String errorMessage) {
        while (true) {
            System.out.print(prompt);
            try {
                long value = Long.parseLong(scanner.nextLine().trim());
                if (validator.test(value)) {
                    return value;
                } else {
                    System.err.println(errorMessage);
                }
            } catch (NumberFormatException e) {
                System.err.println("Введите целое число!");
            }
        }
    }

    private static boolean readValidatedBoolean(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("true") || input.equals("false")) {
                return Boolean.parseBoolean(input);
            } else {
                System.err.println("Введите true или false!");
            }
        }
    }

    private static MeleeWeapon readWeapon(Scanner scanner) {
        System.out.println("Доступное оружие: POWER_SWORD, CHAIN_AXE, MANREAPER, LIGHTING_CLAW, POWER_BLADE");
        while (true) {
            System.out.print("Введите тип оружия (или нажмите Enter для пропуска): ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return null;
            }
            try {
                return MeleeWeapon.valueOf(input.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Неверный тип оружия! Попробуйте еще раз.");
            }
        }
    }

    private static Chapter readChapter(Scanner scanner) {
        String name = readValidatedString(scanner, "Введите название главы: ", s -> !s.trim().isEmpty(), "Название главы не может быть пустым!");

        System.out.print("Введите мир главы (или нажмите Enter для пропуска): ");
        String world = scanner.nextLine().trim();
        if (world.isEmpty()) world = null;

        return new Chapter(name, world);
    }
}
