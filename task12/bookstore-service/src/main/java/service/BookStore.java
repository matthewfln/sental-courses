package service;

import model.Book;
import model.Order;
import enums.BookStatus;
import enums.OrderStatus;
import enums.BookSortType;
import enums.OrderSortType;
import enums.RequestSortType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.io.Serializable;
import util.Config;
import dao.BookDao;
import dao.OrderDao;
import util.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class BookStore implements Serializable {

    private static final long serialVersionUID = 1L;
    private final List<Book> books = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();

    private final transient BookDao bookDao = new BookDao();
    private final transient OrderDao orderDao = new OrderDao();

    public List<Book> getBooks() {
        return books;
    }
    public List<Order> getOrders() {
        return orders;
    }

    // --- Управление складом и книгами ---
    public void addBook(Book book) {
        Book existing = findBookByTitle(book.title);
        if (existing != null) {
            existing.status = BookStatus.IN_STOCK;
            if (Config.getInstance().isAutoFulfillRequests()) {
                existing.requestCount = 0;
                existing.hasRequest = false;
                System.out.println("[Склад] Книга '" + book.title + "' снова в наличии. Запросы закрыты.");
            } else {
                System.out.println("[Склад] Книга '" + book.title + "' снова в наличии. Запросы не закрыты (согласно конфигурации).");
            }
        } else {
            book.status = BookStatus.IN_STOCK;
            if (Config.getInstance().isAutoFulfillRequests()) {
                book.requestCount = 0;
                book.hasRequest = false;
            }
            books.add(book);
            System.out.println("[Склад] Новая книга добавлена: " + book.title);
        }
    }

    public void writeOffBook(Book book) {
        book.status = BookStatus.OUT_OF_STOCK;
        System.out.println("[Склад] Книга списана: " + book.title);
    }

    public void requestBook(Book book) {
        if (book.status == BookStatus.OUT_OF_STOCK) {
            book.requestCount++;
            book.hasRequest = true;
            System.out.println("[Магазин] Оставлен запрос на отсутствующую книгу: " + book.title);
        }
    }

    public Book findBookByTitle(String title) {
        return books.stream()
                .filter(b -> b.title.equals(title))
                .findFirst()
                .orElse(null);
    }

    public Book findBookById(int id) {
        return books.stream()
                .filter(b -> b.id == id)
                .findFirst()
                .orElse(null);
    }

    // --- Сохранение, обновление (для CSV-импорта) ---
    public void saveOrUpdateBook(Book newBook) {
        Book existing = findBookById(newBook.id);
        if (existing != null) {
            existing.copyFrom(newBook);
        } else {
            books.add(newBook);
        }
    }

    public void saveOrUpdateOrder(Order newOrder) {
        for (Order order : orders) {
            if (order.id == newOrder.id) {
                order.copyFrom(newOrder);
                return;
            }
        }
        orders.add(newOrder);
    }

    // --- Управление заказами ---
    public Order createOrder(Book book, String customerName) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        Order order = null;

        try {
            conn.setAutoCommit(false);
            System.out.println("[Транзакция] Начало оформления заказа для: " + customerName);

            // Ищем или сохраняем книгу
            Book stored = findBookByTitle(book.title);
            if (stored == null) {
                stored = book;
                books.add(stored);
                bookDao.save(stored);
            }

            // Если книги нет в наличии, оставляем запрос (меняем книгу в БД)
            if (stored.status == BookStatus.OUT_OF_STOCK) {
                stored.requestCount++;
                stored.hasRequest = true;
                bookDao.update(stored);
                System.out.println("[Транзакция] Счетчик запросов книги увеличен до: " + stored.requestCount);
            }

            // Создаем заказ
            int randomOrderId = (new java.util.Random()).nextInt(1000000);
            order = new Order(randomOrderId, stored, customerName);
            orders.add(order);
            orderDao.save(order);

            conn.commit();
            System.out.println("[Транзакция] ✔ Заказ успешно оформлен и закоммичен в БД!");
        } catch (Exception e) {
            // При любой ошибке - откатываем назад
            try {
                System.out.println("[Транзакция] ❌ Произошла ошибка: " + e.getMessage() + ". Выполняется Rollback!");
                conn.rollback();
            } catch (SQLException ex) {
                System.out.println("[Транзакция] Критическая ошибка при откате: " + ex.getMessage());
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("[Транзакция] Ошибка сброса autoCommit: " + e.getMessage());
            }
        }

        return order;
    }

    public void changeOrderStatus(Order order, OrderStatus newStatus, LocalDate date) {
        if (newStatus == OrderStatus.COMPLETED && order.book.requestCount > 0) {
            System.out.println("[Магазин] ОШИБКА: Заказ нельзя завершить. Ожидается поставка: " + order.book.title);
            return;
        }

        order.status = newStatus;
        if (newStatus == OrderStatus.COMPLETED) {
            order.executionDate = date;
        }
        System.out.println("[Магазин] Статус заказа '" + order.book.title + "' изменен на " + newStatus);
    }

    public Order findOrderByBookTitle(String title) {
        return orders.stream()
                .filter(o -> o.book.title.equals(title))
                .findFirst()
                .orElse(null);
    }

    // --- Вывод с сортировкой ---
    public void printBooks(BookSortType sortType) {
        List<Book> sorted = new ArrayList<>(books);

        Comparator<Book> comparator = switch (sortType) {
            case ALPHABET -> Comparator.comparing(b -> b.title);
            case PUBLICATION_DATE -> Comparator.comparing(b -> b.publicationDate);
            case PRICE -> Comparator.comparingInt(b -> b.price);
            case STATUS -> Comparator.comparing(b -> b.status);
        };

        sorted.sort(comparator);
        for (Book b : sorted) {
            System.out.println(b.title + " | Цена: " + b.price
                    + " | Статус: " + b.status
                    + " | Издана: " + b.publicationDate);
        }
    }

    public void printOrders(OrderSortType sortType) {
        List<Order> sorted = new ArrayList<>(orders);

        Comparator<Order> comparator = switch (sortType) {
            case EXECUTION_DATE -> Comparator.comparing(
                    o -> o.executionDate, Comparator.nullsLast(Comparator.naturalOrder()));
            case PRICE -> Comparator.comparingInt(o -> o.price);
            case STATUS -> Comparator.comparing(o -> o.status);
        };

        sorted.sort(comparator);
        for (Order o : sorted) {
            System.out.println("Заказчик: " + o.customerName
                    + " | Цена: " + o.price
                    + " | Статус: " + o.status
                    + " | Дата: " + o.executionDate);
        }
    }

    public void printRequests(RequestSortType sortType) {
        List<Book> requested = new ArrayList<>();
        for (Book b : books) {
            if (b.requestCount > 0) {
                requested.add(b);
            }
        }

        Comparator<Book> comparator = switch (sortType) {
            case COUNT -> Comparator.comparingInt((Book b) -> b.requestCount).reversed();
            case ALPHABET -> Comparator.comparing(b -> b.title);
        };

        requested.sort(comparator);
        for (Book b : requested) {
            System.out.println(b.title + " | Количество запросов: " + b.requestCount);
        }
    }

    // --- Аналитика ---
    public void printCompletedOrdersStats(LocalDate start, LocalDate end) {
        int count = 0;
        int sum = 0;
        for (Order o : orders) {
            if (o.status == OrderStatus.COMPLETED && o.executionDate != null
                    && !o.executionDate.isBefore(start) && !o.executionDate.isAfter(end)) {
                System.out.println(" - Книга: " + o.book.title
                        + " | Сумма: " + o.price
                        + " | Выполнен: " + o.executionDate);
                count++;
                sum += o.price;
            }
        }
        System.out.println("Итого выполнено: " + count + " шт. на сумму: " + sum + " руб.");
    }

    public void printStaleBooks(LocalDate currentDate) {
        int staleMonths = Config.getInstance().getStaleMonths();
        for (Book b : books) {
            if (b.status == BookStatus.IN_STOCK && b.arrivalDate != null
                    && b.arrivalDate.plusMonths(staleMonths).isBefore(currentDate)) {
                System.out.println(b.title + " | Лежит на складе с " + b.arrivalDate);
            }
        }
    }

    public void printOrderDetails(Order order) {
        System.out.println("Заказчик: " + order.customerName);
        System.out.println("Книга: " + order.book.title);
        System.out.println("Описание книги: " + order.book.description);
        System.out.println("Статус: " + order.status);
    }

    public void printBookDescription(Book book) {
        System.out.println("Книга: \"" + book.title + "\"");
        System.out.println("Описание: " + book.description);
    }
}
