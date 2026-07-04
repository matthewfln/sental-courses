package util;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public final class JpaUtil {
    private static EntityManagerFactory emf;

    private JpaUtil() {
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            emf = Persistence.createEntityManagerFactory("bookstorePU");
        }
        return emf;
    }

    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
