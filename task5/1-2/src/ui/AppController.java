package ui;

import enums.BookStatus;
import enums.OrderStatus;
import model.Book;
import model.Order;
import service.BookStore;

public class AppController {
    private BookStore store;
    private ConsoleView view;

    public AppController(BookStore store, ConsoleView view) {
        this.store = store;
        this.view = view;
    }

    // Запуск главного меню
    public void start() {
        while (true) {
            view.printMainMenu();
            int choice = view.getUserChoice();

            if (choice == 1) {
                handleBooksMenu();
            } else if (choice == 2) {
                handleOrdersMenu();
            } else if (choice == 0) {
                view.printMessage("Завершение работы программы...");
                break; // Выход из бесконечного цикла
            } else {
                view.printMessage("Ошибка: неверный пункт меню.");
            }
        }
    }

    // Логика меню книг
    private void handleBooksMenu() {
        while (true) {
            view.printBooksMenu();
            int choice = view.getUserChoice();

            if (choice == 1) {
                String title = view.getInputString("Введите название новой книги: ");
                store.addBook(new Book(title, BookStatus.IN_STOCK));
            } else if (choice == 2) {
                String title = view.getInputString("Введите название списываемой книги: ");

                Book book = store.findBookByTitle(title);
                if (book != null) {
                    store.writeOffBook(book);
                }
            } else if (choice == 0) {
                break; // Возврат в главное меню
            } else {
                view.printMessage("Ошибка ввода.");
            }
        }
    }

    // Логика меню заказов
    private void handleOrdersMenu() {
        while (true) {
            view.printOrdersMenu();
            int choice = view.getUserChoice();

            if (choice == 1) {
                String title = view.getInputString("Введите название заказываемой книги: ");
                store.createOrder(new Book(title, BookStatus.OUT_OF_STOCK));
            } else if (choice == 2) {
                String title = view.getInputString("Введите название книги для завершения заказа: ");

                Order order = store.findOrderByBookTitle(title);
                if (order != null) {
                    store.changeOrderStatus(order, OrderStatus.COMPLETED);
                }
            } else if (choice == 0) {
                break; // Возврат в главное меню
            } else {
                view.printMessage("Ошибка ввода.");
            }
        }
    }
}