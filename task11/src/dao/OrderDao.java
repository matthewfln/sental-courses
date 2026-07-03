package dao;

import enums.OrderStatus;
import model.Order;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderDao implements GenericDao<Order, Integer> {

    private static final String INSERT_SQL = "INSERT INTO orders (id, book_id, status, customer_name, execution_date, price) VALUES (?, ?, ?, ?, ?, ?);";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM orders WHERE id = ?;";
    private static final String SELECT_ALL_SQL = "SELECT * FROM orders;";
    private static final String UPDATE_SQL = "UPDATE orders SET book_id = ?, status = ?, customer_name = ?, execution_date = ?, price = ? WHERE id = ?;";
    private static final String DELETE_SQL = "DELETE FROM orders WHERE id = ?;";

    private final Connection connection = DatabaseConnection.getInstance().getConnection();
    private final BookDao bookDao = new BookDao();

    @Override
    public void save(Order order) {
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_SQL)) {
            stmt.setInt(1, order.id);
            stmt.setInt(2, order.getBookId());
            stmt.setString(3, order.status.name());
            stmt.setString(4, order.customerName);
            stmt.setDate(5, toSqlDate(order.executionDate));
            stmt.setInt(6, order.price);

            stmt.executeUpdate();
            System.out.println("[OrderDao] Заказ успешно сохранен в БД для клиента: " + order.customerName);
        } catch (SQLException e) {
            System.out.println("[OrderDao] Ошибка сохранения заказа: " + e.getMessage());
        }
    }

    @Override
    public Order findById(Integer id) {
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToOrder(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("[OrderDao] Ошибка поиска заказа по id: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                orders.add(mapRowToOrder(rs));
            }
        } catch (SQLException e) {
            System.out.println("[OrderDao] Ошибка получения списка заказов: " + e.getMessage());
        }
        return orders;
    }

    @Override
    public void update(Order order) {
        try (PreparedStatement stmt = connection.prepareStatement(UPDATE_SQL)) {
            stmt.setInt(1, order.getBookId());
            stmt.setString(2, order.status.name());
            stmt.setString(3, order.customerName);
            stmt.setDate(4, toSqlDate(order.executionDate));
            stmt.setInt(5, order.price);
            stmt.setInt(6, order.id);

            stmt.executeUpdate();
            System.out.println("[OrderDao] Заказ ID " + order.id + " успешно обновлен.");
        } catch (SQLException e) {
            System.out.println("[OrderDao] Ошибка обновления заказа: " + e.getMessage());
        }
    }

    @Override
    public void delete(Integer id) {
        try (PreparedStatement stmt = connection.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("[OrderDao] Заказ с ID " + id + " удален из БД.");
        } catch (SQLException e) {
            System.out.println("[OrderDao] Ошибка удаления заказа: " + e.getMessage());
        }
    }

    private Order mapRowToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.id = rs.getInt("id");
        order.book = bookDao.findById(rs.getInt("book_id"));
        order.status = OrderStatus.valueOf(rs.getString("status"));
        order.customerName = rs.getString("customer_name");
        order.executionDate = toLocalDate(rs.getDate("execution_date"));
        order.price = rs.getInt("price");
        return order;
    }

    // Вспомогательные методы для работы с датами
    private Date toSqlDate(LocalDate date) {
        return (date != null) ? Date.valueOf(date) : null;
    }

    private LocalDate toLocalDate(Date date) {
        return (date != null) ? date.toLocalDate() : null;
    }
}