package ui;

import enums.BookSortType;
import enums.BookStatus;
import enums.OrderStatus;
import model.Book;
import model.Order;
import service.BookStore;
import java.time.LocalDate;

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

    private void handleBooksSort() {
        view.printBooksSortMenu();
        int sortChoice = view.getUserChoice();

        switch (sortChoice) {
            case 1 -> store.printBooks(BookSortType.ALPHABET);
            case 2 -> store.printBooks(BookSortType.PUBLICATION_DATE);
            case 3 -> store.printBooks(BookSortType.PRICE);
            case 4 -> store.printBooks(BookSortType.STATUS);
            default -> view.printMessage("Ошибка: неверный тип сортировки.");
        }
    }

    // Логика меню книг
    private void handleBooksMenu() {
        while (true) {
            view.printBooksMenu();
            int choice = view.getUserChoice();

            if (choice == 1) {
                String title = view.getInputString("Введите название новой книги: ");
                store.addBook(new Book(title, BookStatus.IN_STOCK, 500, java.time.LocalDate.now(), java.time.LocalDate.now(), "Описание"));
            } else if (choice == 2) {
                String title = view.getInputString("Введите название списываемой книги: ");
                Book book = store.findBookByTitle(title);
                if (book != null) {
                    store.writeOffBook(book);
                }
            } else if (choice == 3) {
                handleBooksSort(); // Вызов нового метода сортировки
            } else if (choice == 0) {
                break;
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
                String customer = view.getInputString("Введите имя заказчика: ");

                Book requestedBook = new Book(title, BookStatus.OUT_OF_STOCK, 0, LocalDate.now(), null, "Ожидается поставка");
                store.createOrder(requestedBook, customer);

            } else if (choice == 2) {
                String title = view.getInputString("Введите название книги для завершения заказа: ");
                Order order = store.findOrderByBookTitle(title);
                if (order != null) {
                    store.changeOrderStatus(order, OrderStatus.COMPLETED, LocalDate.now());
                }
            } else if (choice == 0) {
                break;
            } else {
                view.printMessage("Ошибка ввода.");
            }
        }
    }
}