import dao.BookDao;
import dao.OrderDao;
import enums.BookStatus;
import enums.OrderStatus;
import model.Book;
import model.Order;
import util.DatabaseConnection;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

public final class JdbcTest {

    private JdbcTest() {
    }

    public static void main(String[] args) {
        BookDao bookDao = new BookDao();
        OrderDao orderDao = new OrderDao();
        Random random = new Random();

        // 1. Подготовка: создаем случайные ID
        int bookId = (new java.util.Random()).nextInt(1000000);
        int orderId = (new java.util.Random()).nextInt(1000000);

        // 2. Тест CREATE (Сохранение)
        System.out.println("--- 1. Тест CREATE ---");
        Book book = new Book(bookId, "Алгоритмы", BookStatus.IN_STOCK, 1400,
                LocalDate.of(2017, 5, 10), LocalDate.now(), "Иллюстрированное пособие.");
        bookDao.save(book);

        Order order = new Order(orderId, book, "Иван Иванов");
        orderDao.save(order);

        // 3. Тест READ (Чтение)
        System.out.println("\n--- 2. Тест READ ---");
        Book foundBook = bookDao.findById(bookId);
        if (foundBook != null) {
            System.out.println("Найдена книга: " + foundBook.title + " | Цена: " + foundBook.price);
        }

        List<Order> orders = orderDao.findAll();
        System.out.println("Всего заказов в базе: " + orders.size());
        for (Order o : orders) {
            String bookTitle = (o.book != null)
                    ? o.book.title : "нет";
            System.out.println(" -> Заказ #" + o.id + " | Клиент: " + o.customerName + " | Книга: " + bookTitle);
        }

        // 4. Тест UPDATE (Обновление)
        System.out.println("\n--- 3. Тест UPDATE ---");
        Order foundOrder = orderDao.findById(orderId);
        if (foundOrder != null) {
            foundOrder.status = OrderStatus.COMPLETED;
            foundOrder.executionDate = LocalDate.now();
            orderDao.update(foundOrder);
            System.out.println("Новый статус заказа: " + orderDao.findById(orderId).status);
        }

        // 5. Тест DELETE (Удаление)
        System.out.println("\n--- 4. Тест DELETE ---");
        orderDao.delete(orderId);
        bookDao.delete(bookId); // Книгу удаляем строго после заказа

        // 6. Завершение работы
        DatabaseConnection.getInstance().closeConnection();
    }
}
