package repository;

import domain.Entity;
import exceptions.RepositoryException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class BinaryFileRepository<E extends Entity> extends InMemoryRepository<E> {
    private final Path path;

    public BinaryFileRepository(Path path) {
        this.path = path;
        load();
    }

    private void load() {
        if (!Files.exists(path))
            return;
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(path))) {
            var list = (ArrayList<E>) in.readObject();
            super.clear();
            for (E e : list)
                super.add(e);
        } catch (IOException | ClassNotFoundException e) {
            throw new RepositoryException("Error in binary read: " + path, e);
        }
    }

    private void persist() {
        try {
            if (path.getParent() != null)
                Files.createDirectories(path.getParent());
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(
                    path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))) {
                out.writeObject(super.getAll());
            }
        } catch (IOException e) {
            throw new RepositoryException("Error in binary save: " + path, e);
        }
    }

    @Override
    public void add(E element) {
        super.add(element);
        persist();
    }

    @Override
    public void delete(int id) {
        super.delete(id);
        persist();
    }

    @Override
    public void update(E element) {
        super.update(element);
        persist();
    }

    @Override
    public void clear() {
        super.clear();
        persist();
    }
}
