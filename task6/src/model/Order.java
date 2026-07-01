package model;

import enums.OrderStatus;
import java.time.LocalDate;

public class Order {
    public int id;
    public Book book;
    public OrderStatus status;
    public String customerName;
    public LocalDate executionDate;
    public int price;

    public Order(int id, Book book, String customerName) {
        this.id = id;
        this.book = book;
        this.status = OrderStatus.NEW;
        this.customerName = customerName;
        this.price = book.price;
    }
}