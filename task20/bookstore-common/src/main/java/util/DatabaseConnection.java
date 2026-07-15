package util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class DatabaseConnection {

    private static final Logger LOGGER = LogManager.getLogger(DatabaseConnection.class);
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            Properties props = new Properties();
            try (InputStreamReader reader = new InputStreamReader(
                    DatabaseConnection.class.getClassLoader().getResourceAsStream("config.properties"),
                    StandardCharsets.UTF_8)) {
                props.load(reader);
            }

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String pass = props.getProperty("db.password");

            this.connection = DriverManager.getConnection(url, user, pass);
            LOGGER.info("Соединение с базой данных успешно установлено!");
        } catch (SQLException | IOException e) {
            LOGGER.error("Ошибка при подключении к БД: {}", e.getMessage(), e);
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
                LOGGER.debug("Соединение с БД закрыто.");
            } catch (SQLException e) {
                LOGGER.error("Ошибка при закрытии соединения: {}", e.getMessage(), e);
            }
        }
    }
}
