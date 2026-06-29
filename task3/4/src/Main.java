enum BookStatus {
    IN_STOCK,
    OUT_OF_STOCK
}

enum OrderStatus {
    NEW,
    COMPLETED,
    CANCELED
}

class Book {
    public String title;
    public BookStatus status;
    public boolean hasRequest = false; // Отслеживает наличие незакрытого запроса

    public Book(String title, BookStatus status) {
        this.title = title;
        this.status = status;
    }
}

class Order {
    public Book book;
    public OrderStatus status;

    public Order(Book book) {
        this.book = book;
        this.status = OrderStatus.NEW;
    }
}






class BookStore {
    // Добавить книгу на склад
    public void addBook(Book book) {
        book.status = BookStatus.IN_STOCK;
        book.hasRequest = false;
        System.out.println("[Склад] Книга добавлена: " + book.title + ". Запросы закрыты.");
    }

    // Списать книгу со склада
    public void writeOffBook(Book book) {
        book.status = BookStatus.OUT_OF_STOCK;
        System.out.println("[Склад] Книга списана: " + book.title);
    }

    // Оставить запрос на книгу
    public void requestBook(Book book) {
        if (book.status == BookStatus.OUT_OF_STOCK) {
            book.hasRequest = true;
            System.out.println("[Магазин] Оставлен запрос на отсутствующую книгу: " + book.title);
        }
    }

    // Создать заказ
    public Order createOrder(Book book) {
        Order order = new Order(book);
        System.out.println("[Магазин] Создан заказ на книгу: " + book.title);

        // При создании заказа с книгой, которой нет в наличии - автоматически создается запрос
        if (book.status == BookStatus.OUT_OF_STOCK) {
            requestBook(book);
        }
        return order;
    }

    // Изменить статус заказа
    public void changeOrderStatus(Order order, OrderStatus newStatus) {
        // Заказ не может быть завершен, пока запрос книги не будет выполнен
        if (newStatus == OrderStatus.COMPLETED && order.book.hasRequest) {
            System.out.println("[Магазин] ОШИБКА: Заказ нельзя завершить. Ожидается поставка: " + order.book.title);
            return;
        }

        order.status = newStatus;
        System.out.println("[Магазин] Статус заказа '" + order.book.title + "' изменен на " + newStatus);
    }
}



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