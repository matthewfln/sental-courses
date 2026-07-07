package util;

import model.Book;
import model.Order;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

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

                // 1. Создаем объект конфигурации Hibernate
                Configuration configuration = new Configuration();

                // 2. Задаем настройки подключения
                Properties settings = new Properties();
                settings.put(Environment.DRIVER, "org.postgresql.Driver");
                settings.put(Environment.URL, "jdbc:postgresql://localhost:5432/bookstore");
                settings.put(Environment.USER, "postgres");
                settings.put(Environment.PASS, "postgres");

                // Настройки поведения самого Hibernate
                settings.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQL10Dialect");
                settings.put(Environment.HBM2DDL_AUTO, "update");
                settings.put(Environment.SHOW_SQL, "false");
                settings.put(Environment.FORMAT_SQL, "true");

                configuration.setProperties(settings);

                // 3. Перечисляем  сущности с аннотацией @Entity
                configuration.addAnnotatedClass(Book.class);
                configuration.addAnnotatedClass(Order.class);

                // 4. Создаем ServiceRegistry для управления службами Hibernate
                StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties())
                        .build();

                // 5. Строим SessionFactory
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
