package model;

import enums.BookStatus;
import java.time.LocalDate;

public class Book {
    public int id;
    public String title;
    public BookStatus status;
    public int requestCount = 0;
    public boolean hasRequest = false;
    public int price;
    public LocalDate publicationDate;
    public LocalDate arrivalDate;
    public String description;

    public Book(int id, String title, BookStatus status, int price,
                LocalDate publicationDate, LocalDate arrivalDate, String description) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.price = price;
        this.publicationDate = publicationDate;
        this.arrivalDate = arrivalDate;
        this.description = description;
    }
}