package controller;

import enums.BookStatus;
import enums.OrderStatus;
import exception.GlobalExceptionHandler;
import model.Book;
import model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import service.BookStore;
import java.time.LocalDate;
import java.util.Collections;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AnalyticsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookStore bookStore;

    @InjectMocks
    private AnalyticsController analyticsController;

    private Book staleBook;
    private Order completedOrder;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(analyticsController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        staleBook = new Book(1, "Old Book", BookStatus.IN_STOCK, 500,
                LocalDate.of(2018, 1, 1), LocalDate.of(2020, 1, 1), "Description");

        completedOrder = new Order(1, staleBook, OrderStatus.COMPLETED, "Иван", LocalDate.of(2023, 5, 10), 500);
    }

    @Test
    @DisplayName("getStaleBooks - Позитивный: Возвращает список DTO залежалых книг и статус 200 OK")
    void getStaleBooks_ReturnsStaleBooksList() throws Exception {
        when(bookStore.getStaleBooks()).thenReturn(Collections.singletonList(staleBook));

        mockMvc.perform(get("/api/analytics/stale-books")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Old Book"))
                .andExpect(jsonPath("$[0].price").value(500));

        verify(bookStore, times(1)).getStaleBooks();
    }

    @Test
    @DisplayName("getStaleBooks - Негативный: Ошибка в сервисе вызывает статус 500 Internal Server Error")
    void getStaleBooks_ServiceError_ReturnsInternalServerError() throws Exception {
        when(bookStore.getStaleBooks()).thenThrow(new RuntimeException("Database timeout"));

        mockMvc.perform(get("/api/analytics/stale-books")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"));

        verify(bookStore, times(1)).getStaleBooks();
    }

    @Test
    @DisplayName("getCompletedOrders - Позитивный: Возвращает выполненные заказы за период и статус 200 OK")
    void getCompletedOrders_ValidParams_ReturnsOrdersList() throws Exception {
        when(bookStore.getCompletedOrdersByPeriod(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31)))
                .thenReturn(Collections.singletonList(completedOrder));

        mockMvc.perform(get("/api/analytics/completed-orders")
                        .param("start", "2023-01-01")
                        .param("end", "2023-12-31")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].customerName").value("Иван"))
                .andExpect(jsonPath("$[0].status").value("COMPLETED"));

        verify(bookStore, times(1)).getCompletedOrdersByPeriod(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31));
    }

    // Проверяем перехват IllegalArgumentException из сервиса
    @Test
    @DisplayName("getCompletedOrders - Негативный: Неверные аргументы (например, некорректный период) вызывают статус 400 Bad Request")
    void getCompletedOrders_InvalidPeriod_ReturnsBadRequest() throws Exception {
        when(bookStore.getCompletedOrdersByPeriod(any(), any()))
                .thenThrow(new IllegalArgumentException("Дата начала не может быть позже даты окончания"));

        mockMvc.perform(get("/api/analytics/completed-orders")
                        .param("start", "2023-12-31")
                        .param("end", "2023-01-01")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Дата начала не может быть позже даты окончания"));

        verify(bookStore, times(1)).getCompletedOrdersByPeriod(any(), any());
    }
}
