package model;

import enums.OrderStatus;
import java.time.LocalDate;
import java.io.Serializable;

public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
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

    @Override
    public String toString() {
        return id + ";" +
                book.id + ";" +
                status + ";" +
                customerName + ";" +
                executionDate + ";" +
                price;
    }

    public void copyFrom(Order other) {
        this.id = other.id;
        this.book = other.book;
        this.status = other.status;
        this.customerName = other.customerName;
        this.executionDate = other.executionDate;
        this.price = other.price;
    }
}