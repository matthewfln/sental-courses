package model;

import enums.BookStatus;
import java.time.LocalDate;
import java.io.Serializable;

public class Book implements Serializable {
    private static final long serialVersionUID = 1L;
    public int id;
    public String title;
    public BookStatus status;
    public int requestCount = 0;
    public boolean hasRequest = false;
    public int price;
    public LocalDate publicationDate;
    public LocalDate arrivalDate;
    public String description;

    public Book() {
    }

    public Book(int id, String title, BookStatus status, int price, LocalDate publicationDate, LocalDate arrivalDate, String description) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.price = price;
        this.publicationDate = publicationDate;
        this.arrivalDate = arrivalDate;
        this.description = description;
    }

    @Override
    public String toString() {
        return id + ";" +
                title + ";" +
                status + ";" +
                requestCount + ";" +
                hasRequest + ";" +
                price + ";" +
                publicationDate + ";" +
                arrivalDate + ";" +
                description;
    }

    public void copyFrom(Book other) {
        this.id = other.id;
        this.title = other.title;
        this.status = other.status;
        this.requestCount = other.requestCount;
        this.hasRequest = other.hasRequest;
        this.price = other.price;
        this.publicationDate = other.publicationDate;
        this.arrivalDate = other.arrivalDate;
        this.description = other.description;
    }
}