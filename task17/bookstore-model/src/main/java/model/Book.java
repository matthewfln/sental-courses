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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "title", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookStatus status;

    @Column(name = "request_count")
    private int requestCount = 0;

    @Column(name = "has_request")
    private boolean hasRequest = false;

    @Column(name = "price")
    private int price;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @Column(name = "arrival_date")
    private LocalDate arrivalDate;

    @Column(name = "description", length = 1000)
    private String description;

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
