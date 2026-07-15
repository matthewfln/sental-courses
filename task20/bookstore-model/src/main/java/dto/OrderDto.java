package dto;

import enums.OrderStatus;
import model.Order;
import java.time.LocalDate;

public class OrderDto {

    private int id;
    private int bookId;
    private String bookTitle;
    private OrderStatus status;
    private String customerName;
    private LocalDate executionDate;
    private int price;

    public OrderDto() {
    }

    public static OrderDto fromEntity(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        if (order.getBook() != null) {
            dto.setBookId(order.getBook().getId());
            dto.setBookTitle(order.getBook().getTitle());
        }
        dto.setStatus(order.getStatus());
        dto.setCustomerName(order.getCustomerName());
        dto.setExecutionDate(order.getExecutionDate());
        dto.setPrice(order.getPrice());
        return dto;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public LocalDate getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(LocalDate executionDate) {
        this.executionDate = executionDate;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
