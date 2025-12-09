package repository;
import domain.Entity;
import exceptions.NotFoundException;

import java.util.*;

public class InMemoryRepository<E extends Entity> implements InterfaceRepository<E> {
    private final List<E> elements;

    public InMemoryRepository() {
        this.elements = new ArrayList<>();
    }

    @Override
    public void add(E element) {
        elements.add(element);
    }

    @Override
    public void update(E element) {
        if (!exists(element.getId()))
            throw new NotFoundException("The element with ID " + element.getId() + " does not exist.");

        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).getId() == element.getId()) {
                elements.set(i, element);
                return;
            }
        }
    }

    @Override
    public void delete(int id) {
        boolean removed = elements.removeIf(element -> element.getId() == id);
        if (!removed) {
            throw new NotFoundException("The element with ID " + id + " does not exist.");
        }
    }

    @Override
    public boolean exists(int id) {
        for (E element : elements) {
            if (element.getId() == id)
                return true;
        }
        return false;
    }

    @Override
    public ArrayList<E> getAll() {
        return new ArrayList<>(elements);
    }

    @Override
    public E getById(int id) {
        if (!exists(id))
            throw new NotFoundException("The element with ID " + id + " does not exist.");

        for (E element : elements) {
            if (element.getId() == id)
                return element;
        }
        return null;
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public void clear() {
        elements.clear();
    }
}
