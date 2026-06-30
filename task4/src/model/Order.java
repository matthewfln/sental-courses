package model;
import enums.OrderStatus;

public class Order {
    public Book book;
    public OrderStatus status;

    public Order(Book book) {
        this.book = book;
        this.status = OrderStatus.NEW;
    }
}