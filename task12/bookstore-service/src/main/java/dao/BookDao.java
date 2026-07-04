package dao;

import enums.BookStatus;
import model.Book;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookDao implements GenericDao<Book, Integer> {

    private static final String INSERT_SQL = "INSERT INTO books (id, title, status, request_count, has_request, price, publication_date, arrival_date, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM books WHERE id = ?;";
    private static final String SELECT_ALL_SQL = "SELECT * FROM books;";
    private static final String UPDATE_SQL = "UPDATE books SET title = ?, status = ?, request_count = ?, has_request = ?, price = ?, publication_date = ?, arrival_date = ?, description = ? WHERE id = ?;";
    private static final String DELETE_SQL = "DELETE FROM books WHERE id = ?;";

    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    @Override
    public void save(Book book) {
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_SQL)) {
            stmt.setInt(1, book.id);
            stmt.setString(2, book.title);
            stmt.setString(3, book.status.name());
            stmt.setInt(4, book.requestCount);
            stmt.setBoolean(5, book.hasRequest);
            stmt.setInt(6, book.price);
            stmt.setDate(7, toSqlDate(book.publicationDate));
            stmt.setDate(8, toSqlDate(book.arrivalDate));
            stmt.setString(9, book.description);

            stmt.executeUpdate();
            System.out.println("[BookDao] Книга успешно сохранена в БД: " + book.title);
        } catch (SQLException e) {
            System.out.println("[BookDao] Ошибка сохранения книги: " + e.getMessage());
        }
    }

    @Override
    public Book findById(Integer id) {
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToBook(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("[BookDao] Ошибка поиска книги по id: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                books.add(mapRowToBook(rs));
            }
        } catch (SQLException e) {
            System.out.println("[BookDao] Ошибка получения списка книг: " + e.getMessage());
        }
        return books;
    }

    @Override
    public void update(Book book) {
        try (PreparedStatement stmt = connection.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, book.title);
            stmt.setString(2, book.status.name());
            stmt.setInt(3, book.requestCount);
            stmt.setBoolean(4, book.hasRequest);
            stmt.setInt(5, book.price);
            stmt.setDate(6, toSqlDate(book.publicationDate));
            stmt.setDate(7, toSqlDate(book.arrivalDate));
            stmt.setString(8, book.description);
            stmt.setInt(9, book.id);

            stmt.executeUpdate();
            System.out.println("[BookDao] Книга успешно обновлена: " + book.title);
        } catch (SQLException e) {
            System.out.println("[BookDao] Ошибка обновления книги: " + e.getMessage());
        }
    }

    @Override
    public void delete(Integer id) {
        try (PreparedStatement stmt = connection.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("[BookDao] Книга с ID " + id + " удалена из БД.");
        } catch (SQLException e) {
            System.out.println("[BookDao] Ошибка удаления книги: " + e.getMessage());
        }
    }

    private Book mapRowToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.id = rs.getInt("id");
        book.title = rs.getString("title");
        book.status = BookStatus.valueOf(rs.getString("status"));
        book.requestCount = rs.getInt("request_count");
        book.hasRequest = rs.getBoolean("has_request");
        book.price = rs.getInt("price");
        book.publicationDate = toLocalDate(rs.getDate("publication_date"));
        book.arrivalDate = toLocalDate(rs.getDate("arrival_date"));
        book.description = rs.getString("description");
        return book;
    }

    // Вспомогательные методы для работы с датами
    private Date toSqlDate(LocalDate date) {
        return (date != null) ? Date.valueOf(date) : null;
    }

    private LocalDate toLocalDate(Date date) {
        return (date != null) ? date.toLocalDate() : null;
    }
}
