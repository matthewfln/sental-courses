import dao.BookDao;
import dao.OrderDao;
import enums.BookStatus;
import enums.OrderStatus;
import model.Book;
import model.Order;
import util.JpaUtil;
import java.time.LocalDate;
import java.util.List;

public final class JpaTest {
    private JpaTest() {
    }

    public static void main(String[] args) {
        BookDao bookDao = new BookDao();
        OrderDao orderDao = new OrderDao();

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
            System.out.println("Найдена книга: " + foundBook.title + " | Цена: " + foundBook.price);
        }

        List<Order> orders = orderDao.findAll();
        System.out.println("Всего заказов в базе: " + orders.size());
        for (Order o : orders) {
            String bookTitle = (o.book != null) ? o.book.title : "нет";
            System.out.println(" -> Заказ #" + o.id + " | Клиент: " + o.customerName + " | Книга: " + bookTitle);
        }

        System.out.println("\n--- 3. Тест UPDATE ---");
        Order foundOrder = orderDao.findById(orderId);
        if (foundOrder != null) {
            foundOrder.status = OrderStatus.COMPLETED;
            foundOrder.executionDate = LocalDate.now();
            orderDao.update(foundOrder);
            System.out.println("Новый статус заказа: " + orderDao.findById(orderId).status);
        }

        System.out.println("\n--- 4. Тест DELETE ---");
        orderDao.delete(orderId);
        bookDao.delete(bookId);

        JpaUtil.close();
    }
}
