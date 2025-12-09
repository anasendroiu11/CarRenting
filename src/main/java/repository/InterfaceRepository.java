package repository;
import domain.Entity;
import java.util.*;

public interface InterfaceRepository<E extends Entity> {
    void add(E element);
    void update(E element);
    void delete(int id);
    boolean exists(int id);
    E getById(int id);
    ArrayList<E> getAll();
    int size();
    void clear();
}
