package controller;

import dto.OrderDto;
import enums.OrderStatus;
import model.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import service.BookStore;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final BookStore store;

    public OrderController(BookStore store) {
        this.store = store;
    }

    @GetMapping
    public List<OrderDto> getAllOrders() {
        return store.getOrders().stream()
                .map(OrderDto::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public OrderDto getOrderById(@PathVariable int id) {
        Order order = store.findOrderById(id);
        return OrderDto.fromEntity(order);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto createOrder(@RequestParam int bookId, @RequestParam String customerName) {
        Order order = store.createOrder(bookId, customerName);
        return OrderDto.fromEntity(order);
    }

    @PutMapping("/{id}/status")
    public OrderDto changeStatus(@PathVariable int id, @RequestParam OrderStatus status) {
        Order updated = store.changeOrderStatus(id, status);
        return OrderDto.fromEntity(updated);
    }
}
