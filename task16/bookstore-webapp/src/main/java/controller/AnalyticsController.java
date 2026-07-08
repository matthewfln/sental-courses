package controller;

import dto.BookDto;
import dto.OrderDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import service.BookStore;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final BookStore store;

    public AnalyticsController(BookStore store) {
        this.store = store;
    }

    @GetMapping("/stale-books")
    public List<BookDto> getStaleBooks() {
        return store.getStaleBooks().stream()
                .map(BookDto::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/completed-orders")
    public List<OrderDto> getCompletedOrders(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return store.getCompletedOrdersByPeriod(start, end).stream()
                .map(OrderDto::fromEntity)
                .collect(Collectors.toList());
    }
}
