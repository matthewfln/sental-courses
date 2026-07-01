package ui;

import enums.BookSortType;
import enums.BookStatus;
import enums.OrderStatus;
import model.Book;
import model.Order;
import service.BookStore;
import service.CsvManager;

import java.time.LocalDate;

public class AppController {
    private BookStore store;
    private ConsoleView view;
    private CsvManager csvManager;

    public AppController(BookStore store, ConsoleView view) {
        this.store = store;
        this.view = view;
        this.csvManager = new CsvManager();
    }

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
                break;
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

    private void handleBooksMenu() {
        while (true) {
            view.printBooksMenu();
            int choice = view.getUserChoice();

            try {
                if (choice == 1) {
                    String title = view.getInputString("Введите название новой книги: ");
                    int id = (new java.util.Random()).nextInt(1000000);
                    Book book = new Book(id, title, BookStatus.IN_STOCK, 500,
                            LocalDate.now(), LocalDate.now(), "Описание");
                    store.addBook(book);
                } else if (choice == 2) {
                    String title = view.getInputString("Введите название списываемой книги: ");
                    Book book = store.findBookByTitle(title);
                    if (book != null) {
                        store.writeOffBook(book);
                    } else {
                        view.printMessage("Книга не найдена.");
                    }
                } else if (choice == 3) {
                    handleBooksSort();
                } else if (choice == 4) {
                    String path = view.getInputString("Введите путь для экспорта (например, books.csv): ");
                    csvManager.exportBooks(store, path);
                } else if (choice == 5) {
                    String path = view.getInputString("Введите путь к файлу для импорта: ");
                    csvManager.importBooks(store, path);
                } else if (choice == 0) {
                    break;
                } else {
                    view.printMessage("Ошибка: неверный пункт меню.");
                }
            } catch (Exception e) {
                view.printMessage("❌ Ошибка операции: " + e.getMessage());
            }
        }
    }

    private void handleOrdersMenu() {
        while (true) {
            view.printOrdersMenu();
            int choice = view.getUserChoice();

            try {
                if (choice == 1) {
                    String title = view.getInputString("Введите название заказываемой книги: ");
                    String customer = view.getInputString("Введите имя заказчика: ");
                    int id = (new java.util.Random()).nextInt(1000000);
                    Book requestedBook = new Book(id, title, BookStatus.OUT_OF_STOCK,
                            0, LocalDate.now(), null, "Ожидается поставка");
                    store.createOrder(requestedBook, customer);
                } else if (choice == 2) {
                    String title = view.getInputString("Введите название книги для завершения заказа: ");
                    Order order = store.findOrderByBookTitle(title);
                    if (order != null) {
                        store.changeOrderStatus(order, OrderStatus.COMPLETED, LocalDate.now());
                    } else {
                        view.printMessage("Заказ не найден.");
                    }
                } else if (choice == 3) {
                    String path = view.getInputString("Введите путь для экспорта (например, orders.csv): ");
                    csvManager.exportOrders(store, path);
                } else if (choice == 4) {
                    String path = view.getInputString("Введите путь к файлу для импорта: ");
                    csvManager.importOrders(store, path);
                } else if (choice == 0) {
                    break;
                } else {
                    view.printMessage("Ошибка: неверный пункт меню.");
                }
            } catch (Exception e) {
                view.printMessage("❌ Ошибка операции: " + e.getMessage());
            }
        }
    }
}