package service;

import model.Book;
import model.Order;
import enums.BookStatus;
import enums.OrderStatus;
import enums.BookSortType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import dao.BookDao;
import dao.OrderDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import dto.BookDto;
import exception.EntityNotFoundException;

@Service
public class BookStore {

    private static final Logger LOGGER = LogManager.getLogger(BookStore.class);

    private final BookDao bookDao;
    private final OrderDao orderDao;
    private final int staleMonths;
    private final boolean autoFulfillRequests;

    @Autowired
    public BookStore(BookDao bookDao,
                     OrderDao orderDao,
                     @Value("${stale.months:6}") int staleMonths,
                     @Value("${requests.complete.on.arrival:true}") boolean autoFulfillRequests) {
        this.bookDao = bookDao;
        this.orderDao = orderDao;
        this.staleMonths = staleMonths;
        this.autoFulfillRequests = autoFulfillRequests;
    }

    @Transactional(readOnly = true)
    public List<Book> getBooks() {
        return bookDao.findAll();
    }

    @Transactional(readOnly = true)
    public List<Order> getOrders() {
        return orderDao.findAll();
    }

    @Transactional(readOnly = true)
    public Book findBookById(int id) {
        Book book = bookDao.findById(id);
        if (book == null) {
            throw new EntityNotFoundException("Книга с ID " + id + " не найдена.");
        }
        return book;
    }

    @Transactional(readOnly = true)
    public Order findOrderById(int id) {
        Order order = orderDao.findById(id);
        if (order == null) {
            throw new EntityNotFoundException("Заказ с ID " + id + " не найден.");
        }
        return order;
    }

    @Transactional
    public Book createAndAddBook(BookDto dto) {
        int id = (new java.util.Random()).nextInt(1000000);
        Book book = new Book(
                id,
                dto.getTitle(),
                BookStatus.IN_STOCK,
                dto.getPrice(),
                dto.getPublicationDate() != null ? dto.getPublicationDate() : LocalDate.now(),
                LocalDate.now(),
                dto.getDescription()
        );
        return addBook(book);
    }

    @Transactional
    public Book addBook(Book book) {
        Book existing = findBookByTitle(book.getTitle());
        if (existing != null) {
            existing.setStatus(BookStatus.IN_STOCK);
            if (this.autoFulfillRequests) {
                existing.setRequestCount(0);
                existing.setHasRequest(false);
                LOGGER.info("[Склад] Книга '{}' снова в наличии. Запросы закрыты.", book.getTitle());
            }
            bookDao.update(existing);
            return existing;
        } else {
            book.setStatus(BookStatus.IN_STOCK);
            if (this.autoFulfillRequests) {
                book.setRequestCount(0);
                book.setHasRequest(false);
            }
            bookDao.save(book);
            LOGGER.info("[Склад] Новая книга добавлена: {}", book.getTitle());
            return book;
        }
    }

    @Transactional
    public void writeOffBook(int bookId) {
        Book book = bookDao.findById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Книга с ID " + bookId + " не найдена.");
        }
        book.setStatus(BookStatus.OUT_OF_STOCK);
        bookDao.update(book);
        LOGGER.info("[Склад] Книга списана: {}", book.getTitle());
    }

    @Transactional(readOnly = true)
    public Book findBookByTitle(String title) {
        return bookDao.findAll().stream()
                .filter(b -> b.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .orElse(null);
    }

    @Transactional
    public Order createOrder(int bookId, String customerName) {
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя клиента не может быть пустым.");
        }
        LOGGER.debug("Начало оформления заказа для клиента: {}", customerName);

        Book stored = bookDao.findById(bookId);
        if (stored == null) {
            throw new IllegalArgumentException("Книга с ID " + bookId + " не найдена.");
        }

        if (stored.getStatus() == BookStatus.OUT_OF_STOCK) {
            stored.setRequestCount(stored.getRequestCount() + 1);
            stored.setHasRequest(true);
            bookDao.update(stored);
        }

        int randomOrderId = (new java.util.Random()).nextInt(1000000);
        Order order = new Order(randomOrderId, stored, customerName);
        orderDao.save(order);

        LOGGER.info("✔ Заказ #{} успешно оформлен!", order.getId());
        return order;
    }

    @Transactional
    public Order changeOrderStatus(int orderId, OrderStatus newStatus) {
        Order managedOrder = orderDao.findById(orderId);
        if (managedOrder == null) {
            throw new IllegalArgumentException("Заказ с ID " + orderId + " не найден.");
        }

        if (newStatus == OrderStatus.COMPLETED && managedOrder.getBook().getRequestCount() > 0) {
            throw new IllegalStateException("Заказ нельзя завершить. Ожидается поставка книги: " + managedOrder.getBook().getTitle());
        }

        managedOrder.setStatus(newStatus);
        if (newStatus == OrderStatus.COMPLETED) {
            managedOrder.setExecutionDate(LocalDate.now());
        }
        orderDao.update(managedOrder);
        LOGGER.info("[Магазин] Статус заказа #{} изменен на {}", managedOrder.getId(), newStatus);
        return managedOrder;
    }

    @Transactional(readOnly = true)
    public List<Book> getSortedBooks(BookSortType sortType) {
        List<Book> sorted = new ArrayList<>(bookDao.findAll());
        Comparator<Book> comparator = switch (sortType) {
            case ALPHABET -> Comparator.comparing(Book::getTitle);
            case PUBLICATION_DATE -> Comparator.comparing(Book::getPublicationDate);
            case PRICE -> Comparator.comparingInt(Book::getPrice);
            case STATUS -> Comparator.comparing(Book::getStatus);
        };
        sorted.sort(comparator);
        return sorted;
    }

    @Transactional(readOnly = true)
    public List<Order> getCompletedOrdersByPeriod(LocalDate start, LocalDate end) {
        List<Order> result = new ArrayList<>();
        for (Order o : orderDao.findAll()) {
            if (o.getStatus() == OrderStatus.COMPLETED && o.getExecutionDate() != null
                    && !o.getExecutionDate().isBefore(start) && !o.getExecutionDate().isAfter(end)) {
                result.add(o);
            }
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<Book> getStaleBooks() {
        List<Book> result = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        for (Book b : bookDao.findAll()) {
            if (b.getStatus() == BookStatus.IN_STOCK && b.getArrivalDate() != null
                    && b.getArrivalDate().plusMonths(this.staleMonths).isBefore(currentDate)) {
                result.add(b);
            }
        }
        return result;
    }
}
