package model;

import enums.BookStatus;
import java.time.LocalDate;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "books")
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    public int id;

    @Column(name = "title", nullable = false)
    public String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    public BookStatus status;

    @Column(name = "request_count")
    public int requestCount = 0;

    @Column(name = "has_request")
    public boolean hasRequest = false;

    @Column(name = "price")
    public int price;

    @Column(name = "publication_date")
    public LocalDate publicationDate;

    @Column(name = "arrival_date")
    public LocalDate arrivalDate;

    @Column(name = "description", length = 1000)
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
        return id
                + ";" + title
                + ";" + status
                + ";" + requestCount
                + ";" + hasRequest
                + ";" + price
                + ";" + publicationDate
                + ";" + arrivalDate
                + ";" + description;
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
