import dao.BookDao;
import enums.BookStatus;
import model.Book;
import service.BookStore;
import util.DatabaseConnection;

import java.time.LocalDate;
import java.util.Random;

public final class TransactionTest {

    private TransactionTest() {
    }

    public static void main(String[] args) {
        BookStore store = new BookStore();
        BookDao bookDao = new BookDao();

        // 1. Подготовка: создаем книгу, которой нет в наличии
        int bookId = new Random().nextInt(1000000);
        Book book = new Book(bookId, "Рефакторинг", BookStatus.OUT_OF_STOCK, 2000,
                LocalDate.of(2019, 3, 15), null, "Книга Мартина Фаулера");
        bookDao.save(book);
        store.getBooks().add(book);

        // 2. Проверяем успешный заказ
        System.out.println("--- Тест 1: Успешный заказ ---");
        store.createOrder(book, "Иван Иванов");

        Book afterSuccess = bookDao.findById(bookId);
        System.out.println("Запросов на книгу: " + afterSuccess.requestCount);

        // 3. Проверяем откат (Rollback) при ошибке
        System.out.println("\n--- Тест 2: Заказ с ошибкой (имя = null) ---");
        try {
            store.createOrder(book, null);
        } catch (Exception ignored) {
        }

        Book afterFail = bookDao.findById(bookId);
        System.out.println("Запросов на книгу: " + afterFail.requestCount); // 1

        // 4. Итог теста
        if (afterFail.requestCount == 1) {
            System.out.println("✔ ТЕСТ ПРОЙДЕН: транзакция откатилась, лишний запрос не добавился.");
        } else {
            System.out.println("❌ ОШИБКА: счетчик изменился вопреки сбою.");
        }

        // 5. Очистка данных
        bookDao.delete(bookId);
        DatabaseConnection.getInstance().closeConnection();
    }
}
