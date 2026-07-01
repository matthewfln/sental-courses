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

public class BookStore {
    private final List<Book> books = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();

    // --- Управление складом и книгами ---

    public void addBook(Book book) {
        book.status = BookStatus.IN_STOCK;
        book.requestCount = 0; // При поступлении закрываем все ожидающие запросы

        if (!books.contains(book)) {
            books.add(book);
        }
        System.out.println("[Склад] Книга добавлена: " + book.title + ". Запросы закрыты.");
    }

    public void writeOffBook(Book book) {
        book.status = BookStatus.OUT_OF_STOCK;
        System.out.println("[Склад] Книга списана: " + book.title);
    }

    public void requestBook(Book book) {
        if (book.status == BookStatus.OUT_OF_STOCK) {
            book.requestCount++;
            System.out.println("[Магазин] Оставлен запрос на отсутствующую книгу: " + book.title);
        }
    }

    public Book findBookByTitle(String title) {
        return books.stream()
                .filter(book -> book.title.equals(title))
                .findFirst()
                .orElse(null);
    }

    // --- Управление заказами ---

    public Order createOrder(Book book, String customerName) {
        Order order = new Order(book, customerName);
        orders.add(order);
        System.out.println("[Магазин] Создан заказ на книгу: " + book.title + " для " + customerName);

        // Если книги нет, автоматически формируем запрос
        if (book.status == BookStatus.OUT_OF_STOCK) {
            requestBook(book);
        }
        return order;
    }

    public void changeOrderStatus(Order order, OrderStatus newStatus, LocalDate date) {
        // Заказ нельзя завершить, если книга еще не поступила (есть незакрытые запросы)
        if (newStatus == OrderStatus.COMPLETED && order.book.requestCount > 0) {
            System.out.println("[Магазин] ОШИБКА: Заказ нельзя завершить. Ожидается поставка: " + order.book.title);
            return;
        }

        order.status = newStatus;
        if (newStatus == OrderStatus.COMPLETED) {
            order.executionDate = date; // фиксируем дату выполнения
        }
        System.out.println("[Магазин] Статус заказа '" + order.book.title + "' изменен на " + newStatus);
    }

    public Order findOrderByBookTitle(String title) {
        return orders.stream()
                .filter(order -> order.book.title.equals(title))
                .findFirst()
                .orElse(null);
    }

    // --- Вывод списков с сортировкой (пожелание преподавателя) ---

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
            System.out.println(b.title + " | Цена: " + b.price + " | Статус: " + b.status + " | Издана: " + b.publicationDate);
        }
    }

    public void printOrders(OrderSortType sortType) {
        List<Order> sorted = new ArrayList<>(orders);

        Comparator<Order> comparator = switch (sortType) {
            case EXECUTION_DATE -> Comparator.comparing(o -> o.executionDate, Comparator.nullsLast(Comparator.naturalOrder()));
            case PRICE -> Comparator.comparingInt(o -> o.price);
            case STATUS -> Comparator.comparing(o -> o.status);
        };

        sorted.sort(comparator);

        for (Order o : sorted) {
            System.out.println("Заказчик: " + o.customerName + " | Цена: " + o.price + " | Статус: " + o.status + " | Дата: " + o.executionDate);
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

    // --- Аналитика и статистика (из Task 4) ---

    public void printCompletedOrdersStats(LocalDate start, LocalDate end) {
        int count = 0;
        int sum = 0;

        for (Order o : orders) {
            if (o.status == OrderStatus.COMPLETED && o.executionDate != null) {
                if (!o.executionDate.isBefore(start) && !o.executionDate.isAfter(end)) {
                    System.out.println(" - Книга: " + o.book.title + " | Сумма: " + o.price + " | Выполнен: " + o.executionDate);
                    count++;
                    sum += o.price;
                }
            }
        }
        System.out.println("Итого выполнено: " + count + " шт. на общую сумму: " + sum + " руб.");
    }

    public void printStaleBooks(LocalDate currentDate) {
        for (Book b : books) {
            if (b.status == BookStatus.IN_STOCK && b.arrivalDate != null) {
                if (b.arrivalDate.plusMonths(6).isBefore(currentDate)) {
                    System.out.println(b.title + " | Лежит на складе с " + b.arrivalDate);
                }
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