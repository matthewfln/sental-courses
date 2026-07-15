package dto;

import enums.BookStatus;
import model.Book;
import java.time.LocalDate;

public class BookDto {

    private int id;
    private String title;
    private BookStatus status;
    private int price;
    private LocalDate publicationDate;
    private String description;

    public BookDto() {
    }

    public BookDto(int id, String title, BookStatus status, int price, LocalDate publicationDate, String description) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.price = price;
        this.publicationDate = publicationDate;
        this.description = description;
    }

    public static BookDto fromEntity(Book b) {
        return new BookDto(b.getId(), b.getTitle(), b.getStatus(), b.getPrice(), b.getPublicationDate(), b.getDescription());
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
