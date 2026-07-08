package dao;

import model.Order;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class OrderDao implements GenericDao<Order, Integer> {

    private static final Logger LOGGER = LogManager.getLogger(OrderDao.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public void save(Order order) {
        em.persist(order);
        LOGGER.info("[OrderDao] Заказ успешно сохранен в БД для клиента: {}", order.getCustomerName());
    }

    @Override
    public Order findById(Integer id) {
        return em.find(Order.class, id);
    }

    @Override
    public List<Order> findAll() {
        TypedQuery<Order> query = em.createQuery("SELECT o FROM Order o", Order.class);
        return query.getResultList();
    }

    @Override
    public void update(Order order) {
        em.merge(order);
        LOGGER.info("[OrderDao] Заказ ID {} успешно обновлен.", order.getId());
    }

    @Override
    public void delete(Integer id) {
        Order order = em.find(Order.class, id);
        if (order != null) {
            em.remove(order);
            LOGGER.info("[OrderDao] Заказ с ID {} удален из БД.", id);
        } else {
            LOGGER.warn("[OrderDao] Не удалось удалить заказ: заказ с ID {} не найден.", id);
        }
    }
}
