import enums.BookStatus;
import enums.OrderStatus;
import model.Book;
import model.Order;
import service.BookStore;

import java.time.LocalDate;
import java.util.Comparator;

public class Main {
    public static void main(String[] args) {
        BookStore store = new BookStore();

        Book harryPotter = new Book("Гарри Поттер", BookStatus.IN_STOCK, 500,
                LocalDate.of(1997, 6, 26), LocalDate.of(2023, 1, 10), "История о мальчике со шрамом и магии.");
        Book lordOfTheRings = new Book("Властелин Колец", BookStatus.OUT_OF_STOCK, 700,
                LocalDate.of(1954, 7, 29), LocalDate.of(2023, 5, 1), "Эпическое приключение в Средиземье за Кольцо Всевластия.");
        Book javaBasics = new Book("Основы Java", BookStatus.IN_STOCK, 900,
                LocalDate.of(2022, 11, 15), LocalDate.of(2022, 4, 1), "Полноценный учебник по программированию для начинающих.");

        // Добавляем книги на склад
        store.addBook(harryPotter);
        store.addBook(lordOfTheRings);
        store.writeOffBook(lordOfTheRings);
        store.addBook(javaBasics);

        System.out.println("\n--- Операции магазина ---");
        // 1. Создание заказов
        Order o1 = store.createOrder(harryPotter, "Иван Иванов");
        Order o2 = store.createOrder(lordOfTheRings, "Петр Петров");
        Order o3 = store.createOrder(lordOfTheRings, "Анна Сидорова");
        Order o4 = store.createOrder(javaBasics, "Елена Петрова");

        // 2. Изменение статусов и отмена
        store.changeOrderStatus(o1, OrderStatus.COMPLETED, LocalDate.of(2024, 2, 15));
        store.changeOrderStatus(o4, OrderStatus.COMPLETED, LocalDate.of(2024, 3, 20));
        store.changeOrderStatus(o3, OrderStatus.CANCELED, null);
        store.changeOrderStatus(o2, OrderStatus.COMPLETED, LocalDate.of(2024, 2, 20));

        // 3. Оставить ручной запрос на книгу
        store.requestBook(lordOfTheRings);

        // 4. Списание книги со склада
        store.writeOffBook(javaBasics);

        store.printBooks(Comparator.comparing(b -> b.title)); // а) Сортировка по алфавиту
        System.out.println();
        store.printBooks(Comparator.comparing(b -> b.publicationDate)); // б) Сортировка по дате издания
        System.out.println();
        store.printBooks(Comparator.comparingInt(b -> b.price)); // в) Сортировка по цене
        System.out.println();
        store.printBooks(Comparator.comparing(b -> b.status)); // г) Сортировка по наличию
        System.out.println();

        store.printOrders(Comparator.comparing(o -> o.executionDate, Comparator.nullsLast(Comparator.naturalOrder()))); //  Просмотр списка заказов - по дате исполнения
        store.printOrders(Comparator.comparingInt(o -> o.price)); //  Просмотр списка заказов - по  цене
        store.printOrders(Comparator.comparing(o -> o.status)); //  Просмотр списка заказов - по статусу заказа

        store.requestBook(javaBasics);
        store.printRequests(Comparator.comparingInt((Book b) -> b.requestCount).reversed()); // Запросы - по количеству
        store.printRequests(Comparator.comparing(b -> b.title)); // Запросы - по алфавиту

        // --- Выполненные заказы за период, количество и сумма ---
        LocalDate startPeriod = LocalDate.of(2024, 1, 1);
        LocalDate endPeriod = LocalDate.of(2024, 4, 1);
        System.out.println("\n[Статистика за период с " + startPeriod + " по " + endPeriod + "]");
        store.printCompletedOrdersStats(startPeriod, endPeriod);

        // --- Список залежавшихся книг (> 6 месяцев) ---
        LocalDate simulationCurrentDate = LocalDate.of(2024, 1, 1);
        System.out.println("\n[Залежавшиеся книги на " + simulationCurrentDate + "]");
        store.printStaleBooks(simulationCurrentDate);

        // --- Детали заказа ---
        System.out.println("\n[Детали заказа №1]");
        store.printOrderDetails(o1);

        // --- Описание книги ---
        System.out.println("\n[Описание книги]");
        store.printBookDescription(harryPotter);
    }
}