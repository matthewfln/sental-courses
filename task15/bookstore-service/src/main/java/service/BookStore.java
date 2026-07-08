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
import java.io.Serializable;
import dao.BookDao;
import dao.OrderDao;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import util.JpaUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BookStore implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LogManager.getLogger(BookStore.class);
    private final List<Book> books = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();

    private final transient BookDao bookDao;
    private final transient OrderDao orderDao;
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

    public BookStore() {
        this.bookDao = null;
        this.orderDao = null;
        this.staleMonths = 6;
        this.autoFulfillRequests = true;
    }

    public List<Book> getBooks() {
        return this.books;
    }

    public List<Order> getOrders() {
        return this.orders;
    }

    // --- Управление складом и книгами ---
    public void addBook(Book book) {
        Book existing = findBookByTitle(book.getTitle());
        if (existing != null) {
            existing.setStatus(BookStatus.IN_STOCK);
            if (this.autoFulfillRequests) {
                existing.setRequestCount(0);
                existing.setHasRequest(false);
                LOGGER.info("[Склад] Книга '{}' снова в наличии. Запросы закрыты.", book.getTitle());
            } else {
                LOGGER.info("[Склад] Книга '{}' снова в наличии. Запросы не закрыты (согласно конфигурации).", book.getTitle());
            }
        } else {
            book.setStatus(BookStatus.IN_STOCK);
            if (this.autoFulfillRequests) {
                book.setRequestCount(0);
                book.setHasRequest(false);
            }
            books.add(book);
            LOGGER.info("[Склад] Новая книга добавлена: {}", book.getTitle());
        }
    }

    public void writeOffBook(Book book) {
        book.setStatus(BookStatus.OUT_OF_STOCK);
        LOGGER.info("[Склад] Книга списана: {}", book.getTitle());
    }

    public Book findBookByTitle(String title) {
        return books.stream()
                .filter(b -> b.getTitle().equals(title))
                .findFirst()
                .orElse(null);
    }

    public Book findBookById(int id) {
        return books.stream()
                .filter(b -> b.getId() == id)
                .findFirst()
                .orElse(null);
    }

    // --- Сохранение, обновление (для CSV-импорта) ---
    public void saveOrUpdateBook(Book newBook) {
        Book existing = findBookById(newBook.getId());
        if (existing != null) {
            existing.copyFrom(newBook);
        } else {
            books.add(newBook);
        }
    }

    public void saveOrUpdateOrder(Order newOrder) {
        for (Order order : orders) {
            if (order.getId() == newOrder.getId()) {
                order.copyFrom(newOrder);
                return;
            }
        }
        orders.add(newOrder);
    }

    // --- Управление заказами ---
    public Order createOrder(Book book, String customerName) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        Order order = null;
        try {
            tx.begin();
            LOGGER.debug("Начало оформления заказа для клиента: {}", customerName);

            Book stored = findBookByTitle(book.getTitle());
            if (stored == null) {
                stored = book;
                books.add(stored);
                em.persist(stored);
            } else {
                stored = em.merge(stored);
            }

            if (stored.getStatus() == BookStatus.OUT_OF_STOCK) {
                stored.setRequestCount(stored.getRequestCount() + 1);
                stored.setHasRequest(true);
                em.merge(stored);
                LOGGER.debug("Счетчик запросов книги '{}' увеличен до: {}", stored.getTitle(), stored.getRequestCount());
            }

            int randomOrderId = (new java.util.Random()).nextInt(1000000);
            order = new Order(randomOrderId, stored, customerName);
            orders.add(order);
            em.persist(order);

            tx.commit();
            LOGGER.info("✔ Заказ #{} успешно оформлен и сохранён в БД!", order.getId());
        } catch (Exception e) {
            if (tx.isActive()) {
                LOGGER.error("❌ Произошла ошибка при оформлении заказа: {}. Выполняется Rollback!", e.getMessage());
                tx.rollback();
            }
            LOGGER.error("Полный стек ошибки транзакции заказа:", e);
        } finally {
            em.close();
        }
        return order;
    }

    public void changeOrderStatus(Order order, OrderStatus newStatus, LocalDate date) {
        if (newStatus == OrderStatus.COMPLETED && order.getBook().getRequestCount() > 0) {
            LOGGER.warn("[Магазин] ОШИБКА: Заказ нельзя завершить. Ожидается поставка: {}", order.getBook().getTitle());
            return;
        }

        order.setStatus(newStatus);
        if (newStatus == OrderStatus.COMPLETED) {
            order.setExecutionDate(date);
        }
        LOGGER.info("[Магазин] Статус заказа '{}' изменен на {}", order.getBook().getTitle(), newStatus);
    }

    public Order findOrderByBookTitle(String title) {
        return orders.stream()
                .filter(o -> o.getBook().getTitle().equals(title))
                .findFirst()
                .orElse(null);
    }

    // --- Вывод с сортировкой ---
    public void printBooks(BookSortType sortType) {
        List<Book> sorted = new ArrayList<>(books);

        Comparator<Book> comparator = switch (sortType) {
            case ALPHABET -> Comparator.comparing(Book::getTitle);
            case PUBLICATION_DATE -> Comparator.comparing(Book::getPublicationDate);
            case PRICE -> Comparator.comparingInt(Book::getPrice);
            case STATUS -> Comparator.comparing(Book::getStatus);
        };

        sorted.sort(comparator);
        for (Book b : sorted) {
            System.out.println(b.getTitle() + " | Цена: " + b.getPrice()
                    + " | Статус: " + b.getStatus()
                    + " | Издана: " + b.getPublicationDate());
        }
    }

    // --- Аналитика ---
    public void printCompletedOrdersStats(LocalDate start, LocalDate end) {
        int count = 0;
        int sum = 0;
        for (Order o : orders) {
            if (o.getStatus() == OrderStatus.COMPLETED && o.getExecutionDate() != null
                    && !o.getExecutionDate().isBefore(start) && !o.getExecutionDate().isAfter(end)) {
                System.out.println(" - Книга: " + o.getBook().getTitle()
                        + " | Сумма: " + o.getPrice()
                        + " | Выполнен: " + o.getExecutionDate());
                count++;
                sum += o.getPrice();
            }
        }
        System.out.println("Итого выполнено: " + count + " шт. на сумму: " + sum + " руб.");
    }

    public void printStaleBooks(LocalDate currentDate) {
        for (Book b : books) {
            if (b.getStatus() == BookStatus.IN_STOCK && b.getArrivalDate() != null
                    && b.getArrivalDate().plusMonths(this.staleMonths).isBefore(currentDate)) {
                System.out.println(b.getTitle() + " | Лежит на складе с " + b.getArrivalDate());
            }
        }
    }

    public void printOrderDetails(Order order) {
        System.out.println("Заказчик: " + order.getCustomerName());
        System.out.println("Книга: " + order.getBook().getTitle());
        System.out.println("Описание книги: " + order.getBook().getDescription());
        System.out.println("Статус: " + order.getStatus());
    }

    public void printBookDescription(Book book) {
        System.out.println("Книга: \"" + book.getTitle() + "\"");
        System.out.println("Описание: " + book.getDescription());
    }
}
