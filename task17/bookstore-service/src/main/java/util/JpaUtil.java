package util;

import model.Book;
import model.Order;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import java.sql.Connection;
import java.sql.DriverManager;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import javax.persistence.EntityManagerFactory;
import java.util.Properties;

public final class JpaUtil {

    private static final Logger LOGGER = LogManager.getLogger(JpaUtil.class);
    private static EntityManagerFactory emf;

    private JpaUtil() {
    }

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            try {
                LOGGER.info("Начало программной конфигурации Hibernate через Java-код...");

                LOGGER.info("Запуск миграций Liquibase...");
                String dbUrl = "jdbc:postgresql://localhost:5432/bookstore";
                String dbUser = "postgres";
                String dbPass = "";

                Class.forName("org.postgresql.Driver");
                try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPass)) {
                    Database database = DatabaseFactory.getInstance()
                            .findCorrectDatabaseImplementation(new JdbcConnection(connection));

                    Liquibase liquibase = new Liquibase(
                            "db/changelog/db.changelog-master.xml",
                            new ClassLoaderResourceAccessor(),
                            database
                    );
                    liquibase.update("");
                    LOGGER.info("Миграции Liquibase успешно применены!");
                } catch (Exception e) {
                    LOGGER.error("Ошибка при выполнении миграций Liquibase: {}", e.getMessage());
                    throw new RuntimeException("Не удалось обновить схему БД через Liquibase", e);
                }

                Configuration configuration = new Configuration();
                Properties settings = new Properties();

                settings.put(Environment.DRIVER, "org.postgresql.Driver");
                settings.put(Environment.URL, dbUrl);
                settings.put(Environment.USER, dbUser);
                settings.put(Environment.PASS, dbPass);

                settings.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQL10Dialect");
                settings.put(Environment.HBM2DDL_AUTO, "validate");
                settings.put(Environment.SHOW_SQL, "false");
                settings.put(Environment.FORMAT_SQL, "true");

                configuration.setProperties(settings);

                configuration.addAnnotatedClass(Book.class);
                configuration.addAnnotatedClass(Order.class);

                StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties())
                        .build();

                emf = configuration.buildSessionFactory(serviceRegistry);
                LOGGER.info("Hibernate SessionFactory успешно создан программно!");
            } catch (Exception e) {
                LOGGER.error("Критическая ошибка при программной настройке Hibernate: {}", e.getMessage(), e);
                throw new RuntimeException("Не удалось инициализировать базу данных", e);
            }
        }
        return emf;
    }

    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            LOGGER.info("Соединение с БД (SessionFactory) успешно закрыто.");
        }
    }
}
