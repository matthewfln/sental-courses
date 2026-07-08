import config.AppConfig;
import dao.BookDao;
import enums.BookStatus;
import model.Book;
import service.BookStore;
import util.DatabaseConnection;
import java.time.LocalDate;
import java.util.Random;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public final class TransactionTest {

    private TransactionTest() {
    }

    public static void main(String[] args) {
        // Инициализируем Spring-контекст вместо ручного создания через new
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        // Получаем бины, управляемые Spring
        BookStore store = context.getBean(BookStore.class);
        BookDao bookDao = context.getBean(BookDao.class);

        // 2. Подготовка: создаем книгу, которой нет в наличии
        int bookId = (new Random()).nextInt(1000000);
        Book book = new Book(bookId, "Рефакторинг", BookStatus.OUT_OF_STOCK, 2000,
                LocalDate.of(2019, 3, 15), null, "Книга Мартина Фаулера");
        bookDao.save(book);
        store.getBooks().add(book);

        // --- Тест 1: Успешный заказ ---
        System.out.println("--- Тест 1: Успешный заказ ---");
        store.createOrder(book, "Иван Иванов");

        Book afterSuccess = bookDao.findById(bookId);
        System.out.println("Запросов на книгу: " + afterSuccess.getRequestCount());

        // --- Тест 2: Заказ с ошибкой (имя = null) ---
        System.out.println("\n--- Тест 2: Заказ с ошибкой (имя = null) ---");
        try {
            store.createOrder(book, null);
        } catch (Exception ignored) {
        }

        Book afterFail = bookDao.findById(bookId);
        System.out.println("Запросов на книгу: " + afterFail.getRequestCount());

        // 4. Итог теста
        if (afterFail.getRequestCount() == 1) {
            System.out.println("✔ ТЕСТ ПРОЙДЕН: транзакция откатилась, лишний запрос не добавился.");
        } else {
            System.out.println("❌ ОШИБКА: счетчик изменился вопреки сбою.");
        }

        // 5. Очистка данных и закрытие ресурсов
        bookDao.delete(bookId);
        DatabaseConnection.getInstance().closeConnection();
        context.close();
    }
}
