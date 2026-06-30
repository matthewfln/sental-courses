package model;
import enums.BookStatus;
import java.time.LocalDate;

public class Book {
    public String title;
    public BookStatus status;
    public boolean hasRequest = false;
    public int publicationYear;
    public double price;
    public LocalDate receiptDate;
    public String description;
    public int requestCount = 0;

    public Book(String title, BookStatus status, int publicationYear, double price, String description) {
        this.title = title;
        this.status = status;
        this.publicationYear = publicationYear;
        this.price = price;
        this.description = description;
        this.receiptDate = LocalDate.now(); // дата поступления  - текущая
    }
}