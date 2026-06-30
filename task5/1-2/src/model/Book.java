package model;
import enums.BookStatus;

public class Book {
    public String title;
    public BookStatus status;
    public boolean hasRequest = false; // Отслеживает наличие незакрытого запроса

    public Book(String title, BookStatus status) {
        this.title = title;
        this.status = status;
    }
}