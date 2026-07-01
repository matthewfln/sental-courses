package ui;

import java.util.Scanner;

public class ConsoleView {

    // -- Singleton --
    private static final ConsoleView INSTANCE = new ConsoleView();

    private final Scanner scanner = new Scanner(System.in);

    public static ConsoleView getInstance() {
        return INSTANCE;
    }
    private ConsoleView() {
    }
    // ----


    // Вывод главного меню
    public void printMainMenu() {
        System.out.println("\n=== Главное меню ===");
        System.out.println("1. Управление книгами");
        System.out.println("2. Управление заказами");
        System.out.println("0. Выход");
        System.out.print("Выберите пункт: ");
    }

    // Вывод меню книг
    public void printBooksMenu() {
        System.out.println("\n--- Управление книгами ---");
        System.out.println("1. Добавить книгу");
        System.out.println("2. Списать книгу");
        System.out.println("3. Вывести список книг (Сортировка)");
        System.out.println("0. Назад");
        System.out.print("Выберите пункт: ");
    }

    public void printBooksSortMenu() {
        System.out.println("  Выберите тип сортировки:");
        System.out.println("  1. По алфавиту");
        System.out.println("  2. По дате издания");
        System.out.println("  3. По цене");
        System.out.println("  4. По статусу наличия");
        System.out.print("  Ваш выбор: ");
    }

    // Вывод меню заказов
    public void printOrdersMenu() {
        System.out.println("\n--- Управление заказами ---");
        System.out.println("1. Создать заказ");
        System.out.println("2. Завершить заказ");
        System.out.println("0. Назад");
        System.out.print("Выберите пункт: ");
    }

    // Безопасное чтение числа
    public int getUserChoice() {
        if (scanner.hasNextInt()) {
            return scanner.nextInt();
        } else {
            scanner.next();
            return -1;
        }
    }

    // Чтение строки (названия книги и т.д)
    public String getInputString(String message) {
        System.out.print(message);
        return new Scanner(System.in).nextLine();
    }

    // Вывод любого сообщения
    public void printMessage(String message) {
        System.out.println(message);
    }
}