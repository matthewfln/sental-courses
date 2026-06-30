import enums.BookStatus;
import enums.OrderStatus;
import model.Book;
import model.Order;
import service.BookStore;
import service.ReportService;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        BookStore store = new BookStore();
        ReportService reportService = new ReportService(store);

        // Доступные книги
        Book harryPotter = new Book("Гарри Поттер", BookStatus.IN_STOCK, 1997, 500.0, "Книга про юного волшебника");
        Book lordOfTheRings = new Book("Властелин Колец", BookStatus.OUT_OF_STOCK, 1954, 750.0, "Эпическое фэнтези");

        store.books.add(harryPotter);
        store.books.add(lordOfTheRings);

        System.out.println("--- 1. Успешный заказ ---");
        Order order1 = store.createOrder(harryPotter, "Алексей Смирнов");
        store.changeOrderStatus(order1, OrderStatus.COMPLETED);

        System.out.println("\n--- 2. Заказ с ожиданием (отсутствует на складе) ---");
        Order order2 = store.createOrder(lordOfTheRings, "Мария Иванова");
        store.changeOrderStatus(order2, OrderStatus.COMPLETED);

        System.out.println("\n--- 3. Поступление книги на склад и завершение заказа ---");
        store.addBook(lordOfTheRings);
        store.changeOrderStatus(order2, OrderStatus.COMPLETED);

        // Демонстрация новых отчетов
        reportService.printBooks();
        reportService.printOrders();
        reportService.printRequests();

        LocalDate monthAgo = LocalDate.now().minusMonths(1);
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        reportService.printStats(monthAgo, tomorrow);

        reportService.printBookDescription(harryPotter);
        reportService.printOrderDetails(order1);
    }
}