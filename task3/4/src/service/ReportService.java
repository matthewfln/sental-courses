package service;

import model.Book;
import model.Order;
import enums.BookStatus;
import enums.OrderStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportService {
    private BookStore store;

    public ReportService(BookStore store) {
        this.store = store;
    }

    // Список книг (по алфавиту)
    public void printBooks() {
        List<Book> list = new ArrayList<>(store.books);
        list.sort((b1, b2) -> b1.title.compareTo(b2.title));

        System.out.println("\n--- Книги ---");
        for (Book b : list) System.out.println(b.title + " | " + b.price + " | " + b.status);
    }

    // Список заказов (сортируем по дате)
    public void printOrders() {
        List<Order> list = new ArrayList<>(store.orders);
        list.sort((o1, o2) -> {
            if (o1.executionDate == null || o2.executionDate == null) return 0;
            return o1.executionDate.compareTo(o2.executionDate);
        });

        System.out.println("\n--- Заказы ---");
        for (Order o : list) System.out.println(o.customerName + " | " + o.book.title + " | " + o.status);
    }

    // Запросы на книги (по количеству по убыванию)
    public void printRequests() {
        List<Book> list = new ArrayList<>(store.books);
        list.sort((b1, b2) -> b2.requestCount - b1.requestCount);

        System.out.println("\n--- Запросы ---");
        for (Book b : list) {
            if (b.requestCount > 0) System.out.println(b.title + " | Запросов: " + b.requestCount);
        }
    }

    // Статистика по заказам
    public void printStats(LocalDate start, LocalDate end) {
        int count = 0;
        double sum = 0;
        System.out.println("\n--- Выполненные заказы ---");

        for (Order o : store.orders) {
            if (o.status == OrderStatus.COMPLETED && o.executionDate != null) {
                if (!o.executionDate.isBefore(start) && !o.executionDate.isAfter(end)) {
                    System.out.println(o.customerName + " | " + o.price);
                    count++;
                    sum += o.price;
                }
            }
        }
        System.out.println("Всего заказов: " + count + ". Заработано: " + sum);
    }

    // Залежавшиеся книги
    public void printStaleBooks() {
        LocalDate oldDate = LocalDate.now().minusMonths(6);
        System.out.println("\n--- Залежавшиеся книги ---");

        for (Book b : store.books) {
            if (b.status == BookStatus.IN_STOCK && b.receiptDate != null && b.receiptDate.isBefore(oldDate)) {
                System.out.println(b.title + " | Дата: " + b.receiptDate);
            }
        }
    }

    // Детали заказа
    public void printOrderDetails(Order o) {
        System.out.println("\nЗаказ: " + o.customerName + " | Книга: " + o.book.title + " | Итого: " + o.price);
    }

    // Описание книги
    public void printBookDescription(Book b) {
        System.out.println("\nКнига: " + b.title + " | Год: " + b.publicationYear + " | Описание: " + b.description);
    }
}