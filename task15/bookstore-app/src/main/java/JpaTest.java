import config.AppConfig;
import dao.BookDao;
import dao.OrderDao;
import enums.BookStatus;
import enums.OrderStatus;
import model.Book;
import model.Order;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import util.JpaUtil;
import java.time.LocalDate;
import java.util.List;

public final class JpaTest {

    private JpaTest() {
    }

    public static void main(String[] args) {

        util.JpaUtil.getEntityManagerFactory();

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        BookDao bookDao = context.getBean(BookDao.class);
        OrderDao orderDao = context.getBean(OrderDao.class);

        int bookId = (new java.util.Random()).nextInt(1000000);
        int orderId = (new java.util.Random()).nextInt(1000000);

        System.out.println("--- 1. Тест CREATE ---");
        Book book = new Book(bookId, "Алгоритмы", BookStatus.IN_STOCK, 1400,
                LocalDate.of(2017, 5, 10), LocalDate.now(), "Иллюстрированное пособие.");
        bookDao.save(book);

        Order order = new Order(orderId, book, "Иван Иванов");
        orderDao.save(order);

        System.out.println("\n--- 2. Тест READ ---");
        Book foundBook = bookDao.findById(bookId);
        if (foundBook != null) {
            System.out.println("Найдена книга: " + foundBook.getTitle() + " | Цена: " + foundBook.getPrice());
        }

        List<Order> orders = orderDao.findAll();
        System.out.println("Всего заказов в базе: " + orders.size());
        for (Order o : orders) {
            String bookTitle = (o.getBook() != null) ? o.getBook().getTitle() : "нет";
            System.out.println(" -> Заказ #" + o.getId() + " | Клиент: " + o.getCustomerName() + " | Книга: " + bookTitle);
        }

        System.out.println("\n--- 3. Тест UPDATE ---");
        Order foundOrder = orderDao.findById(orderId);
        if (foundOrder != null) {
            foundOrder.setStatus(OrderStatus.COMPLETED);
            foundOrder.setExecutionDate(LocalDate.now());
            orderDao.update(foundOrder);
            System.out.println("Новый статус заказа: " + orderDao.findById(orderId).getStatus());
        }

        System.out.println("\n--- 4. Тест DELETE ---");
        orderDao.delete(orderId);
        bookDao.delete(bookId);

        JpaUtil.close();
        context.close();
    }
}
