import enums.BookStatus;
import enums.OrderStatus;
import model.Book;
import model.Order;
import service.BookStore;


public class Main {
    public static void main(String[] args) {
        BookStore store = new BookStore();

        // Доступные книги
        Book harryPotter = new Book("Гарри Поттер", BookStatus.IN_STOCK);
        Book lordOfTheRings = new Book("Властелин Колец", BookStatus.OUT_OF_STOCK);

        System.out.println("--- 1. Успешный заказ ---");
        Order order1 = store.createOrder(harryPotter);
        store.changeOrderStatus(order1, OrderStatus.COMPLETED);

        System.out.println("\n--- 2. Заказ с ожиданием (отсутствует на складе) ---");
        Order order2 = store.createOrder(lordOfTheRings);
        store.changeOrderStatus(order2, OrderStatus.COMPLETED);

        System.out.println("\n--- 3. Поступление книги на склад и завершение заказа ---");
        store.addBook(lordOfTheRings);
        store.changeOrderStatus(order2, OrderStatus.COMPLETED);

        System.out.println("\n--- 4. Списание книги и отмена заказа ---");
        store.writeOffBook(harryPotter);
        Order order3 = store.createOrder(harryPotter);

        // Отменяем заказ
        store.changeOrderStatus(order3, OrderStatus.CANCELED);
    }
}