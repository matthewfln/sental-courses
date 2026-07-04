package dao;

import model.Book;
import util.JpaUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;

public class BookDao implements GenericDao<Book, Integer> {

    private static final Logger LOGGER = LogManager.getLogger(BookDao.class);

    @Override
    public void save(Book book) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(book);
            tx.commit();
            System.out.println("[BookDao] Книга успешно сохранена в БД: " + book.title);
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            LOGGER.error("Ошибка сохранения книги: " + e.getMessage(), e);
            System.out.println("[BookDao] Ошибка сохранения книги: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    @Override
    public Book findById(Integer id) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Book.class, id);
        } catch (Exception e) {
            LOGGER.error("Ошибка поиска книги по id: " + e.getMessage(), e);
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Book> findAll() {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Book> query = em.createQuery("SELECT b FROM Book b", Book.class);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.error("Ошибка получения списка книг: " + e.getMessage(), e);
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Book book) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(book);
            tx.commit();
            System.out.println("[BookDao] Книга успешно обновлена: " + book.title);
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            LOGGER.error("Ошибка обновления книги: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Integer id) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Book book = em.find(Book.class, id);
            if (book != null) {
                em.remove(book);
            }
            tx.commit();
            System.out.println("[BookDao] Книга с ID " + id + " удалена из БД.");
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            LOGGER.error("Ошибка удаления книги: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
