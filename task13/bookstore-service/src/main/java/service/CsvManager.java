package service;

import enums.BookStatus;
import enums.OrderStatus;
import model.Book;
import model.Order;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;

public class CsvManager {

    // --- Книги ---
    public void exportBooks(BookStore store, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (Book book : store.getBooks()) {
                writer.println(book);
            }
            System.out.println("[Экспорт] Книги успешно выгружены в " + filePath);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении книг: " + e.getMessage());
        }
    }

    public void importBooks(BookStore store, String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 9) {
                    int id = Integer.parseInt(parts[0]);
                    String title = parts[1];
                    BookStatus status = BookStatus.valueOf(parts[2]);
                    int requestCount = Integer.parseInt(parts[3]);
                    boolean hasRequest = Boolean.parseBoolean(parts[4]);
                    int price = Integer.parseInt(parts[5]);
                    LocalDate publicationDate = parseDate(parts[6]);
                    LocalDate arrivalDate = parseDate(parts[7]);
                    String description = parts[8];

                    Book imported = new Book(id, title, status, price, publicationDate, arrivalDate, description);
                    imported.requestCount = requestCount;
                    imported.hasRequest = hasRequest;
                    store.saveOrUpdateBook(imported);
                }
            }
            System.out.println("[Импорт] Книги успешно загружены.");
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла книг: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка формата данных в файле книг. Проверьте содержимое CSV.");
        }
    }

    // --- Заказы ---
    public void exportOrders(BookStore store, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (Order order : store.getOrders()) {
                writer.println(order);
            }
            System.out.println("[Экспорт] Заказы успешно выгружены в " + filePath);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении заказов: " + e.getMessage());
        }
    }

    public void importOrders(BookStore store, String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 6) {
                    int id = Integer.parseInt(parts[0]);
                    int bookId = Integer.parseInt(parts[1]);
                    OrderStatus status = OrderStatus.valueOf(parts[2]);
                    String customerName = parts[3];
                    LocalDate executionDate = parseDate(parts[4]);
                    int price = Integer.parseInt(parts[5]);

                    Book book = store.findBookById(bookId);
                    if (book != null) {
                        Order imported = new Order(id, book, customerName);
                        imported.status = status;
                        imported.executionDate = executionDate;
                        imported.price = price;
                        store.saveOrUpdateOrder(imported);
                    } else {
                        System.out.println("[Импорт] Заказ " + id
                                + " пропущен: книга с ID " + bookId + " не найдена.");
                    }
                }
            }
            System.out.println("[Импорт] Заказы успешно загружены.");
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла заказов: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка формата данных в файле заказов. Проверьте содержимое CSV.");
        }
    }


    private LocalDate parseDate(String value) {
        if (value == null || value.equalsIgnoreCase("null") || value.isBlank()) {
            return null;
        }
        return LocalDate.parse(value);
    }
}
