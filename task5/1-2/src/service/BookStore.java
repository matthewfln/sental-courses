package service;

import model.Book;
import model.Order;
import enums.BookStatus;
import enums.OrderStatus;
import java.util.ArrayList;
import java.util.List;



public class BookStore {
    private final List<Book> books = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();

    // Добавить книгу на склад
    public void addBook(Book book) {
        book.status = BookStatus.IN_STOCK;
        book.hasRequest = false;

        books.add(book);

        System.out.println("[Склад] Книга добавлена: " + book.title + ". Запросы закрыты.");
    }

    // Списать книгу со склада
    public void writeOffBook(Book book) {
        book.status = BookStatus.OUT_OF_STOCK;
        System.out.println("[Склад] Книга списана: " + book.title);
    }

    // Оставить запрос на книгу
    public void requestBook(Book book) {
        if (book.status == BookStatus.OUT_OF_STOCK) {
            book.hasRequest = true;
            System.out.println("[Магазин] Оставлен запрос на отсутствующую книгу: " + book.title);
        }
    }

    // Создать заказ
    public Order createOrder(Book book) {
        Order order = new Order(book);
        orders.add(order);

        System.out.println("[Магазин] Создан заказ на книгу: " + book.title);

        // При создании заказа с книгой, которой нет в наличии - автоматически создается запрос
        if (book.status == BookStatus.OUT_OF_STOCK) {
            requestBook(book);
        }
        return order;
    }

    // Изменить статус заказа
    public void changeOrderStatus(Order order, OrderStatus newStatus) {
        // Заказ не может быть завершен, пока запрос книги не будет выполнен
        if (newStatus == OrderStatus.COMPLETED && order.book.hasRequest) {
            System.out.println("[Магазин] ОШИБКА: Заказ нельзя завершить. Ожидается поставка: " + order.book.title);
            return;
        }

        order.status = newStatus;
        System.out.println("[Магазин] Статус заказа '" + order.book.title + "' изменен на " + newStatus);
    }

    // Найти книгу по названию
    public Book findBookByTitle(String title) {
        return books.stream()
                .filter(book -> book.title.equals(title))
                .findFirst()
                .orElse(null);
    }

    // Найти заказ по названию книги
    public Order findOrderByBookTitle(String title) {
        return orders.stream()
                .filter(order -> order.book.title.equals(title))
                .findFirst()
                .orElse(null);
    }
}