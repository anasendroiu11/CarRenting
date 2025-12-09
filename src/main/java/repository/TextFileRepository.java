package repository;

import domain.Entity;
import exceptions.RepositoryException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class TextFileRepository<E extends Entity> extends InMemoryRepository<E> {
    private final Path path;
    private final InterfaceFactory<E> factory;

    public TextFileRepository(Path path, InterfaceFactory<E> factory) {
        this.path = path;
        this.factory = factory;
        load();
    }

    private void load() {
        if (!Files.exists(path))
            return;
        try {
            for (String line : Files.readAllLines(path)) {
                if (line.isBlank())
                    continue;
                String[] tokens = line.split(";");
                super.add(factory.fromTokens(tokens));
            }
        } catch (IOException e) {
            throw new RepositoryException("Error reading text file: " + path, e);
        }
    }

    private void persist() {
        try {
            if (path.getParent() != null)
                Files.createDirectories(path.getParent());
            var lines = new ArrayList<String>();
            for (E e : super.getAll()) {
                lines.add(factory.toLine(e));
            }
            Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RepositoryException("Error writing text file: " + path, e);
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
