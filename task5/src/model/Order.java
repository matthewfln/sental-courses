package model;
import enums.OrderStatus;
import java.time.LocalDate;

public class Order {
    public Book book;
    public OrderStatus status;

    public String customerName;
    public LocalDate executionDate;
    public int price;

    public Order(Book book, String customerName) {
        this.book = book;
        this.status = OrderStatus.NEW;
        this.customerName = customerName;
        this.price = book.price;
    }
}