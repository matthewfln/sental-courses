package service;

import dao.BookDao;
import dao.OrderDao;
import enums.BookSortType;
import enums.BookStatus;
import exception.EntityNotFoundException;
import model.Book;
import model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookStoreTest {

    @Mock
    private BookDao bookDao;

    @Mock
    private OrderDao orderDao;

    private BookStore bookStore;

    private Book testBook;

    @BeforeEach
    void setUp() {
        // Инициализируем сервис с staleMonths = 6 и autoFulfillRequests = true
        bookStore = new BookStore(bookDao, orderDao, 6, true);

        testBook = new Book(
                1, "Java Concurrency", BookStatus.IN_STOCK, 1500,
                LocalDate.of(2020, 1, 1), LocalDate.now(), "Отличная книга"
        );
    }

    // ==========================================
    // Поиск книг (getBooks, findBookById)
    // ==========================================
    @Test
    @DisplayName("getBooks - Позитивный: Возвращает список всех книг из DAO")
    void getBooks_ReturnsAllBooks() {
        when(bookDao.findAll()).thenReturn(Collections.singletonList(testBook));

        List<Book> books = bookStore.getBooks();

        assertNotNull(books);
        assertEquals(1, books.size());
        assertEquals("Java Concurrency", books.get(0).getTitle());
        verify(bookDao, times(1)).findAll();
    }

    @Test
    @DisplayName("findBookById - Позитивный: Книга найдена и успешно возвращена")
    void findBookById_BookExists_ReturnsBook() {
        when(bookDao.findById(1)).thenReturn(testBook);

        Book result = bookStore.findBookById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(bookDao, times(1)).findById(1);
    }

    @Test
    @DisplayName("findBookById - Негативный: Книга не найдена, выбрасывает EntityNotFoundException")
    void findBookById_BookNotFound_ThrowsException() {
        when(bookDao.findById(99)).thenReturn(null);

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> bookStore.findBookById(99)
        );

        assertTrue(ex.getMessage().contains("не найдена"));
        verify(bookDao, times(1)).findById(99);
    }

    // ==========================================
    // Добавление книг (addBook)
    // ==========================================
    @Test
    @DisplayName("addBook - Позитивный: Новая книга сохраняется в DAO со статусом IN_STOCK")
    void addBook_NewBook_SavesBook() {
        when(bookDao.findAll()).thenReturn(Collections.emptyList());

        Book newBook = new Book(2, "Clean Code", BookStatus.OUT_OF_STOCK, 1200, LocalDate.now(), LocalDate.now(), "");
        Book result = bookStore.addBook(newBook);

        assertEquals(BookStatus.IN_STOCK, result.getStatus(), "Статус должен измениться на IN_STOCK");
        assertEquals(0, result.getRequestCount());
        assertFalse(result.isHasRequest());

        verify(bookDao, times(1)).save(newBook);
        verify(bookDao, never()).update(any());
    }

    @Test
    @DisplayName("addBook - Позитивный (Существующая книга): Обновляет статус и сбрасывает запросы")
    void addBook_ExistingBook_UpdatesStatusAndClearsRequests() {
        Book existing = new Book(1, "Java Concurrency", BookStatus.OUT_OF_STOCK, 1500, LocalDate.now(), LocalDate.now(), "");
        existing.setRequestCount(5);
        existing.setHasRequest(true);

        when(bookDao.findAll()).thenReturn(Collections.singletonList(existing));

        Book result = bookStore.addBook(new Book(99, "Java Concurrency", BookStatus.IN_STOCK, 1500, LocalDate.now(), LocalDate.now(), ""));

        assertEquals(BookStatus.IN_STOCK, result.getStatus());
        assertEquals(0, result.getRequestCount(), "Счетчик запросов должен обнулиться");
        assertFalse(result.isHasRequest(), "Флаг запроса должен быть сброшен");

        verify(bookDao, times(1)).update(existing);
        verify(bookDao, never()).save(any());
    }

    // ==========================================
    // Списание книг (writeOffBook)
    // ==========================================
    @Test
    @DisplayName("writeOffBook - Позитивный: Меняет статус книги на OUT_OF_STOCK")
    void writeOffBook_BookExists_UpdatesStatusToOutOfStock() {
        when(bookDao.findById(1)).thenReturn(testBook);

        bookStore.writeOffBook(1);

        assertEquals(BookStatus.OUT_OF_STOCK, testBook.getStatus());
        verify(bookDao, times(1)).update(testBook);
    }

    @Test
    @DisplayName("writeOffBook - Негативный: Книга не найдена, выбрасывает IllegalArgumentException")
    void writeOffBook_BookNotFound_ThrowsException() {
        when(bookDao.findById(99)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> bookStore.writeOffBook(99));
        verify(bookDao, never()).update(any());
    }

    // ==========================================
    // Сортировка и залежалые книги
    // ==========================================
    @Test
    @DisplayName("getSortedBooks - Позитивный: Корректно сортирует книги по цене")
    void getSortedBooks_ByPrice_ReturnsSortedList() {
        Book cheapBook = new Book(2, "A Book", BookStatus.IN_STOCK, 500, LocalDate.now(), LocalDate.now(), "");
        when(bookDao.findAll()).thenReturn(Arrays.asList(testBook, cheapBook)); // testBook стоит 1500

        List<Book> sorted = bookStore.getSortedBooks(BookSortType.PRICE);

        assertEquals(2, sorted.size());
        assertEquals(500, sorted.get(0).getPrice(), "Первой должна идти более дешевая книга");
        assertEquals(1500, sorted.get(1).getPrice());
    }

    @Test
    @DisplayName("getStaleBooks - Позитивный: Находит книги, поступившие более 6 месяцев назад")
    void getStaleBooks_ReturnsOnlyStaleBooks() {
        Book staleBook = new Book(2, "Old Book", BookStatus.IN_STOCK, 1000,
                LocalDate.of(2018, 1, 1), LocalDate.now().minusMonths(7), "");
        Book freshBook = new Book(3, "New Book", BookStatus.IN_STOCK, 1000,
                LocalDate.now(), LocalDate.now().minusMonths(2), "");

        when(bookDao.findAll()).thenReturn(Arrays.asList(staleBook, freshBook));

        List<Book> staleBooks = bookStore.getStaleBooks();

        assertEquals(1, staleBooks.size());
        assertEquals("Old Book", staleBooks.get(0).getTitle());
    }

    // ==========================================
    // Поиск заказов (getOrders, findOrderById)
    // ==========================================
    @Test
    @DisplayName("getOrders - Позитивный: Возвращает список всех заказов")
    void getOrders_ReturnsAllOrders() {
        Order order = new Order(1, testBook, "Иван Иванов");
        when(orderDao.findAll()).thenReturn(Collections.singletonList(order));

        List<Order> orders = bookStore.getOrders();

        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertEquals("Иван Иванов", orders.get(0).getCustomerName());
        verify(orderDao, times(1)).findAll();
    }

    @Test
    @DisplayName("findOrderById - Позитивный: Заказ найден и возвращен")
    void findOrderById_OrderExists_ReturnsOrder() {
        Order order = new Order(1, testBook, "Иван Иванов");
        when(orderDao.findById(1)).thenReturn(order);

        Order result = bookStore.findOrderById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(orderDao, times(1)).findById(1);
    }

    @Test
    @DisplayName("findOrderById - Негативный: Заказ не найден, выбрасывает EntityNotFoundException")
    void findOrderById_OrderNotFound_ThrowsException() {
        when(orderDao.findById(99)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> bookStore.findOrderById(99));
        verify(orderDao, times(1)).findById(99);
    }

    // ==========================================
    // Создание заказов (createOrder)
    // ==========================================
    @Test
    @DisplayName("createOrder - Позитивный (Книга в наличии): Создает заказ со статусом NEW")
    void createOrder_BookInStock_CreatesOrder() {
        when(bookDao.findById(1)).thenReturn(testBook); // testBook имеет статус IN_STOCK

        Order order = bookStore.createOrder(1, "Петр Петров");

        assertNotNull(order);
        assertEquals("Петр Петров", order.getCustomerName());
        assertEquals(enums.OrderStatus.NEW, order.getStatus());
        assertEquals(testBook.getPrice(), order.getPrice());

        verify(orderDao, times(1)).save(any(Order.class));
        verify(bookDao, never()).update(any(Book.class)); // Статус книги не менялся, апдейт не нужен
    }

    @Test
    @DisplayName("createOrder - Позитивный (Книга отсутствует): Создает заказ и увеличивает requestCount")
    void createOrder_BookOutOfStock_CreatesOrderAndIncrementsRequestCount() {
        Book outOfStockBook = new Book(2, "Rare Book", BookStatus.OUT_OF_STOCK, 2000, LocalDate.now(), LocalDate.now(), "");
        when(bookDao.findById(2)).thenReturn(outOfStockBook);

        Order order = bookStore.createOrder(2, "Анна Сидорова");

        assertNotNull(order);
        assertEquals(1, outOfStockBook.getRequestCount(), "Счетчик запросов книги должен вырасти на 1");
        assertTrue(outOfStockBook.isHasRequest(), "Флаг запроса должен стать true");

        verify(bookDao, times(1)).update(outOfStockBook);
        verify(orderDao, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("createOrder - Негативный: Имя клиента пустое, выбрасывает IllegalArgumentException")
    void createOrder_EmptyCustomerName_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> bookStore.createOrder(1, "   "));
        verify(bookDao, never()).findById(anyInt());
        verify(orderDao, never()).save(any());
    }

    // ==========================================
    // Изменение статуса заказа (changeOrderStatus)
    // ==========================================
    @Test
    @DisplayName("changeOrderStatus - Позитивный: Успешно меняет статус на COMPLETED и ставит дату")
    void changeOrderStatus_ToCompleted_UpdatesStatusAndDate() {
        Order order = new Order(1, testBook, "Иван Иванов"); // testBook.requestCount == 0
        when(orderDao.findById(1)).thenReturn(order);

        Order updated = bookStore.changeOrderStatus(1, enums.OrderStatus.COMPLETED);

        assertEquals(enums.OrderStatus.COMPLETED, updated.getStatus());
        assertNotNull(updated.getExecutionDate(), "При завершении заказа должна проставляться дата выполнения");
        verify(orderDao, times(1)).update(order);
    }

    @Test
    @DisplayName("changeOrderStatus - Негативный: Попытка завершить заказ при отсутствии книги (requestCount > 0)")
    void changeOrderStatus_CompletedWhenBookRequested_ThrowsIllegalStateException() {
        testBook.setRequestCount(1); // Имитируем, что книга еще не поступила на склад
        Order order = new Order(1, testBook, "Иван Иванов");
        when(orderDao.findById(1)).thenReturn(order);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> bookStore.changeOrderStatus(1, enums.OrderStatus.COMPLETED)
        );

        assertTrue(ex.getMessage().contains("Ожидается поставка книги"));
        verify(orderDao, never()).update(any());
    }

    // ==========================================
    // Аналитика по заказам (getCompletedOrdersByPeriod)
    // ==========================================
    @Test
    @DisplayName("getCompletedOrdersByPeriod - Позитивный: Фильтрует выполненные заказы по датам")
    void getCompletedOrdersByPeriod_ReturnsFilteredOrders() {
        Order completedOrder = new Order(1, testBook, enums.OrderStatus.COMPLETED, "Иван", LocalDate.of(2023, 5, 10), 1000);
        Order oldOrder = new Order(2, testBook, enums.OrderStatus.COMPLETED, "Петр", LocalDate.of(2020, 1, 1), 1000);
        Order newOrder = new Order(3, testBook, enums.OrderStatus.NEW, "Анна", null, 1000);

        when(orderDao.findAll()).thenReturn(Arrays.asList(completedOrder, oldOrder, newOrder));

        List<Order> result = bookStore.getCompletedOrdersByPeriod(
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31)
        );

        assertEquals(1, result.size());
        assertEquals("Иван", result.get(0).getCustomerName());
    }
}
