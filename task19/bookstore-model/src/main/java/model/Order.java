package model;

import enums.OrderStatus;
import java.time.LocalDate;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private int id; // <-- private вместо public

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id")
    private Book book; // <-- private вместо public

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "execution_date")
    private LocalDate executionDate;

    @Column(name = "price")
    private int price;

    public Order(int id, Book book, OrderStatus status, String customerName, LocalDate executionDate, int price) {
        this.id = id;
        this.book = book;
        this.status = status;
        this.customerName = customerName;
        this.executionDate = executionDate;
        this.price = price;
    }

    public Order(int id, Book book, String customerName) {
        this.id = id;
        this.book = book;
        this.status = OrderStatus.NEW;
        this.customerName = customerName;
        this.price = book.getPrice();
    }

    @Override
    public String toString() {
        return id
                + ";" + book.getId()
                + ";" + status
                + ";" + customerName
                + ";" + executionDate
                + ";" + price;
    }

    public void copyFrom(Order other) {
        this.id = other.id;
        this.book = other.book;
        this.status = other.status;
        this.customerName = other.customerName;
        this.executionDate = other.executionDate;
        this.price = other.price;
    }

    public int getBookId() {
        if (this.book != null) {
            return this.book.getId();
        }
        return 0;
    }
}
