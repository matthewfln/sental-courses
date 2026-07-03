package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("config.properties"));

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String pass = props.getProperty("db.password");

            this.connection = DriverManager.getConnection(url, user, pass);
            System.out.println("[JDBC] Соединение с базой данных успешно установлено!");
        } catch (SQLException | IOException e) {
            System.out.println("[JDBC] Ошибка при подключении к БД: " + e.getMessage());
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("[JDBC] Соединение с БД закрыто.");
            } catch (SQLException e) {
                System.out.println("[JDBC] Ошибка при закрытии соединения: " + e.getMessage());
            }
        }
    }
}