package dao;

import java.util.List;

public interface GenericDao<T, K> {
    void save(T entity);
    T findById(K id);
    List<T> findAll();
    void update(T entity);
    void delete(K id);
}
