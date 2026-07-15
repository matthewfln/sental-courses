package controller;

import enums.BookStatus;
import enums.OrderStatus;
import exception.EntityNotFoundException;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookStore bookStore;

    @InjectMocks
    private OrderController orderController;

    private Order testOrder;
    private Book testBook;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        testBook = new Book(1, "Java Concurrency", BookStatus.IN_STOCK, 1500, LocalDate.now(), LocalDate.now(), "");
        testOrder = new Order(10, testBook, "Иван Иванов");
    }

    @Test
    @DisplayName("getAllOrders - Позитивный: Возвращает список всех заказов и статус 200 OK")
    void getAllOrders_ReturnsOrdersList() throws Exception {
        when(bookStore.getOrders()).thenReturn(Collections.singletonList(testOrder));

        mockMvc.perform(get("/api/orders")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].customerName").value("Иван Иванов"))
                .andExpect(jsonPath("$[0].status").value("NEW"));

        verify(bookStore, times(1)).getOrders();
    }

    @Test
    @DisplayName("getOrderById - Позитивный: Заказ найден, возвращается DTO и статус 200 OK")
    void getOrderById_OrderExists_ReturnsOrderDto() throws Exception {
        when(bookStore.findOrderById(10)).thenReturn(testOrder);

        mockMvc.perform(get("/api/orders/10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.bookTitle").value("Java Concurrency"));

        verify(bookStore, times(1)).findOrderById(10);
    }

    @Test
    @DisplayName("getOrderById - Негативный: Заказ не найден, возвращает 404 Not Found")
    void getOrderById_OrderNotFound_ReturnsNotFound() throws Exception {
        when(bookStore.findOrderById(99)).thenThrow(new EntityNotFoundException("Заказ с ID 99 не найден."));

        mockMvc.perform(get("/api/orders/99")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));

        verify(bookStore, times(1)).findOrderById(99);
    }

    @Test
    @DisplayName("createOrder - Позитивный: Успешно оформляет заказ и возвращает статус 201 Created")
    void createOrder_ValidParams_ReturnsCreatedOrder() throws Exception {
        when(bookStore.createOrder(1, "Иван Иванов")).thenReturn(testOrder);

        mockMvc.perform(post("/api/orders")
                        .param("bookId", "1")
                        .param("customerName", "Иван Иванов")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.customerName").value("Иван Иванов"));

        verify(bookStore, times(1)).createOrder(1, "Иван Иванов");
    }

    @Test
    @DisplayName("createOrder - Негативный: Пустое имя клиента вызывает 400 Bad Request")
    void createOrder_EmptyCustomerName_ReturnsBadRequest() throws Exception {
        when(bookStore.createOrder(anyInt(), anyString()))
                .thenThrow(new IllegalArgumentException("Имя клиента не может быть пустым."));

        mockMvc.perform(post("/api/orders")
                        .param("bookId", "1")
                        .param("customerName", "   ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Имя клиента не может быть пустым."));

        verify(bookStore, times(1)).createOrder(anyInt(), anyString());
    }

    @Test
    @DisplayName("changeStatus - Позитивный: Успешно меняет статус заказа и возвращает 200 OK")
    void changeStatus_ValidStatus_ReturnsUpdatedOrder() throws Exception {
        testOrder.setStatus(OrderStatus.COMPLETED);
        when(bookStore.changeOrderStatus(10, OrderStatus.COMPLETED)).thenReturn(testOrder);

        mockMvc.perform(put("/api/orders/10/status")
                        .param("status", "COMPLETED")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(bookStore, times(1)).changeOrderStatus(10, OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("changeStatus - Негативный: Попытка завершить заказ при отсутствии книги вызывает 409 Conflict")
    void changeStatus_ConflictWhenBookRequested_ReturnsConflict() throws Exception {
        when(bookStore.changeOrderStatus(10, OrderStatus.COMPLETED))
                .thenThrow(new IllegalStateException("Заказ нельзя завершить. Ожидается поставка книги"));

        mockMvc.perform(put("/api/orders/10/status")
                        .param("status", "COMPLETED")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Заказ нельзя завершить. Ожидается поставка книги"));

        verify(bookStore, times(1)).changeOrderStatus(10, OrderStatus.COMPLETED);
    }
}
