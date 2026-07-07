package ui;

import java.util.Scanner;
import org.springframework.stereotype.Component;

@Component
public final class ConsoleView {

    private final Scanner scanner = new Scanner(System.in, java.nio.charset.StandardCharsets.UTF_8);
    public ConsoleView() {
    }

    public void printMainMenu() {
        System.out.println("\n=== Главное меню ===");
        System.out.println("1. Управление книгами");
        System.out.println("2. Управление заказами");
        System.out.println("3. Аналитика магазина");
        System.out.println("0. Выход");
        System.out.print("Выберите пункт: ");
    }

    public void printBooksMenu() {
        System.out.println("\n--- Управление книгами ---");
        System.out.println("1. Добавить книгу");
        System.out.println("2. Списать книгу");
        System.out.println("3. Вывести список книг (Сортировка)");
        System.out.println("4. Экспорт книг в CSV");
        System.out.println("5. Импорт книг из CSV");
        System.out.println("6. Посмотреть описание книги");
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

    public void printOrdersMenu() {
        System.out.println("\n--- Управление заказами ---");
        System.out.println("1. Создать заказ");
        System.out.println("2. Завершить заказ");
        System.out.println("3. Экспорт заказов в CSV");
        System.out.println("4. Импорт заказов из CSV");
        System.out.println("5. Посмотреть детали заказа");
        System.out.println("0. Назад");
        System.out.print("Выберите пункт: ");
    }

    public int getUserChoice() {
        if (scanner.hasNextInt()) {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Очищаем буфер после считывания числа
            return choice;
        } else {
            scanner.next();
            return -1;
        }
    }

    public String getInputString(String message) {
        System.out.print(message);
        return scanner.nextLine(); // Используем уже существующий scanner
    }

    public void printMessage(String message) {
        System.out.println(message);
    }
}
