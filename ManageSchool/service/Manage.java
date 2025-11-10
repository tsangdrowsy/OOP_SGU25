package ManageSchool.service;

import java.util.List;

public interface Manage<T> {
    void add(T obj);
    void update(T obj);
    void remove(String id);
    T findByID(String id);
    List<T> findByName(String name);
    List<T> getAll();
}
