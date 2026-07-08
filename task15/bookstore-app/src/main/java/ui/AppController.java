package ui;

import enums.BookSortType;
import enums.BookStatus;
import enums.OrderStatus;
import model.Book;
import model.Order;
import service.BookStore;
import service.CsvManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class AppController {

    private static final Logger LOGGER = LogManager.getLogger(AppController.class);

    private final BookStore store;
    private final ConsoleView view;
    private final CsvManager csvManager;

    // Внедрение зависимостей через конструктор
    public AppController(BookStore store, ConsoleView view, CsvManager csvManager) {
        this.store = store;
        this.view = view;
        this.csvManager = csvManager;
    }

    public void start() {
        while (true) {
            view.printMainMenu();
            int choice = view.getUserChoice();

            try {
                if (choice == 1) {
                    handleBooksMenu();
                } else if (choice == 2) {
                    handleOrdersMenu();
                } else if (choice == 3) {
                    LOGGER.info("Начало обработки команды: Аналитика магазина");
                    handleAnalytics();
                    LOGGER.info("Команда успешно выполнена: Аналитика магазина");
                } else if (choice == 0) {
                    LOGGER.info("Начало обработки команды: Завершение работы программы");
                    util.AppStateStorage.save(store);
                    view.printMessage("Завершение работы программы...");
                    LOGGER.info("Команда успешно выполнена: Завершение работы программы");
                    break;
                } else {
                    view.printMessage("Ошибка: неверный пункт меню.");
                }
            } catch (Exception e) {
                LOGGER.error("Ошибка при выполнении команды главного меню: " + e.getMessage(), e);
                view.printMessage("❌ Ошибка операции: " + e.getMessage());
            }
        }
    }

    private void handleAnalytics() {
        view.printMessage("\n--- Аналитика магазина ---");
        view.printMessage("Залежавшиеся книги на складе:");
        store.printStaleBooks(LocalDate.now());

        view.printMessage("\nСтатистика выполненных заказов за последний месяц:");
        store.printCompletedOrdersStats(LocalDate.now().minusMonths(1), LocalDate.now());
    }

    private void handleBooksSort() {
        view.printBooksSortMenu();
        int sortChoice = view.getUserChoice();
        switch (sortChoice) {
            case 1:
                store.printBooks(BookSortType.ALPHABET);
                break;
            case 2:
                store.printBooks(BookSortType.PUBLICATION_DATE);
                break;
            case 3:
                store.printBooks(BookSortType.PRICE);
                break;
            case 4:
                store.printBooks(BookSortType.STATUS);
                break;
            default:
                view.printMessage("Ошибка: неверный тип сортировки.");
                break;
        }
    }

    private void handleBooksMenu() {
        while (true) {
            view.printBooksMenu();
            int choice = view.getUserChoice();

            try {
                if (choice == 1) {
                    LOGGER.info("Начало обработки команды: Добавление новой книги");
                    String title = view.getInputString("Введите название новой книги: ");
                    int id = (new java.util.Random()).nextInt(1000000);
                    Book book = new Book(id, title, BookStatus.IN_STOCK, 500,
                    LocalDate.now(), LocalDate.now(), "Описание");
                    store.addBook(book);
                    LOGGER.info("Команда успешно выполнена: Добавление новой книги");
                } else if (choice == 2) {
                    LOGGER.info("Начало обработки команды: Списание книги");
                    String title = view.getInputString("Введите название списываемой книги: ");
                    Book book = store.findBookByTitle(title);
                    if (book != null) {
                        store.writeOffBook(book);
                    } else {
                        view.printMessage("Книга не найдена.");
                    }
                    LOGGER.info("Команда успешно выполнена: Списание книги");
                } else if (choice == 3) {
                    LOGGER.info("Начало обработки команды: Сортировка и просмотр книг");
                    handleBooksSort();
                    LOGGER.info("Команда успешно выполнена: Сортировка и просмотр книг");
                } else if (choice == 4) {
                    LOGGER.info("Начало обработки команды: Экспорт книг в CSV");
                    String path = view.getInputString("Введите путь для экспорта (например, books.csv): ");
                    csvManager.exportBooks(store, path);
                    LOGGER.info("Команда успешно выполнена: Экспорт книг в CSV");
                } else if (choice == 5) {
                    LOGGER.info("Начало обработки команды: Импорт книг из CSV");
                    String path = view.getInputString("Введите путь к файлу для импорта: ");
                    csvManager.importBooks(store, path);
                    LOGGER.info("Команда успешно выполнена: Импорт книг из CSV");
                } else if (choice == 6) {
                    LOGGER.info("Начало обработки команды: Просмотр описания книги");
                    String title = view.getInputString("Введите название книги: ");
                    Book book = store.findBookByTitle(title);
                    if (book != null) {
                        store.printBookDescription(book);
                    } else {
                        view.printMessage("Книга не найдена.");
                    }
                    LOGGER.info("Команда успешно выполнена: Просмотр описания книги");
                } else if (choice == 0) {
                    break;
                } else {
                    view.printMessage("Ошибка: неверный пункт меню.");
                }
            } catch (Exception e) {
                LOGGER.error("Ошибка при обработке команды в меню книг: " + e.getMessage(), e);
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
                    LOGGER.info("Начало обработки команды: Создание заказа");
                    String title = view.getInputString("Введите название заказываемой книги: ");
                    String customer = view.getInputString("Введите имя заказчика: ");
                    int id = (new java.util.Random()).nextInt(1000000);
                    Book requestedBook = new Book(id, title, BookStatus.OUT_OF_STOCK,
                    0, LocalDate.now(), null, "Ожидается поставка");
                    store.createOrder(requestedBook, customer);
                    LOGGER.info("Команда успешно выполнена: Создание заказа");
                } else if (choice == 2) {
                    LOGGER.info("Начало обработки команды: Завершение заказа");
                    String title = view.getInputString("Введите название книги для завершения заказа: ");
                    Order order = store.findOrderByBookTitle(title);
                    if (order != null) {
                        store.changeOrderStatus(order, OrderStatus.COMPLETED, LocalDate.now());
                    } else {
                        view.printMessage("Заказ не найден.");
                    }
                    LOGGER.info("Команда успешно выполнена: Завершение заказа");
                } else if (choice == 3) {
                    LOGGER.info("Начало обработки команды: Экспорт заказов в CSV");
                    String path = view.getInputString("Введите путь для экспорта (например, orders.csv): ");
                    csvManager.exportOrders(store, path);
                    LOGGER.info("Команда успешно выполнена: Экспорт заказов в CSV");
                } else if (choice == 4) {
                    LOGGER.info("Начало обработки команды: Импорт заказов из CSV");
                    String path = view.getInputString("Введите путь к файлу для импорта: ");
                    csvManager.importOrders(store, path);
                    LOGGER.info("Команда успешно выполнена: Импорт заказов из CSV");
                } else if (choice == 5) {
                    LOGGER.info("Начало обработки команды: Просмотр деталей заказа");
                    String title = view.getInputString("Введите название книги из заказа: ");
                    Order order = store.findOrderByBookTitle(title);
                    if (order != null) {
                        store.printOrderDetails(order);
                    } else {
                        view.printMessage("Заказ не найден.");
                    }
                    LOGGER.info("Команда успешно выполнена: Просмотр деталей заказа");
                } else if (choice == 0) {
                    break;
                } else {
                    view.printMessage("Ошибка: неверный пункт меню.");
                }
            } catch (Exception e) {
                LOGGER.error("Ошибка при обработке команды в меню заказов: " + e.getMessage(), e);
                view.printMessage("❌ Ошибка операции: " + e.getMessage());
            }
        }
    }
}
