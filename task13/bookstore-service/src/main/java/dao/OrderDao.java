package dao;

import model.Order;
import util.JpaUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;

public class OrderDao implements GenericDao<Order, Integer> {

    private static final Logger LOGGER = LogManager.getLogger(OrderDao.class);

    @Override
    public void save(Order order) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(order);
            tx.commit();
            System.out.println("[OrderDao] Заказ успешно сохранен в БД для клиента: " + order.customerName);
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            LOGGER.error("Ошибка сохранения заказа: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public Order findById(Integer id) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Order.class, id);
        } catch (Exception e) {
            LOGGER.error("Ошибка поиска заказа по id: " + e.getMessage(), e);
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Order> findAll() {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Order> query = em.createQuery("SELECT o FROM Order o", Order.class);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.error("Ошибка получения списка заказов: " + e.getMessage(), e);
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Order order) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(order);
            tx.commit();
            System.out.println("[OrderDao] Заказ ID " + order.id + " успешно обновлен.");
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            LOGGER.error("Ошибка обновления заказа: " + e.getMessage(), e);
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
            Order order = em.find(Order.class, id);
            if (order != null) {
                em.remove(order);
            }
            tx.commit();
            System.out.println("[OrderDao] Заказ с ID " + id + " удален из БД.");
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            LOGGER.error("Ошибка удаления заказа: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
