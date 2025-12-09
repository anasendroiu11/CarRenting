package repository;

import domain.Car;
import domain.Entity;
import domain.Rental;
import exceptions.NotFoundException;
import exceptions.RepositoryException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class SQLRepository<E extends Entity> implements InterfaceRepository<E> {
    private final String url;
    private final InterfaceFactory<E> factory;
    private final String tableName;

    public SQLRepository(String dbPath, InterfaceFactory<E> factory, String tableName) {
        this.url = "jdbc:sqlite:" + dbPath;
        this.factory = factory;
        this.tableName = tableName;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql;
        if (tableName.equalsIgnoreCase("cars")) {
            sql = """
                    CREATE TABLE IF NOT EXISTS Cars(
                        id INTEGER PRIMARY KEY,
                        brand TEXT NOT NULL,
                        model TEXT NOT NULL
                    )
                    """;
        } else if (tableName.equalsIgnoreCase("rentals")) {
            sql = """
                    CREATE TABLE IF NOT EXISTS Rentals(
                        id INTEGER PRIMARY KEY,
                        carId INTEGER NOT NULL,
                        startDate TEXT NOT NULL,
                        endDate TEXT NOT NULL,
                        FOREIGN KEY(carId) REFERENCES Cars(id)
                    )
                    """;
        } else {
            throw new RepositoryException("Unknown table type: " + tableName);
        }

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RepositoryException("Error in creating table. ", e);
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url);
    }

    @Override
    public void add(E element) {
        String sql;
        if (element instanceof Car car) {
            sql = "INSERT INTO Cars(id, brand, model) VALUES(?, ?, ?)";
            try (Connection conn = connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, car.getId());
                ps.setString(2, car.getBrand());
                ps.setString(3, car.getModel());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RepositoryException("Error at inserting in table Cars. ", e);
            }
        } else if (element instanceof Rental rental) {
            sql = "INSERT INTO Rentals(id, carId, startDate, endDate) VALUES(?, ?, ?, ?)";
            try (Connection conn = connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, rental.getId());
                ps.setInt(2, rental.getCar().getId());
                ps.setString(3, rental.getStartDate().toString());
                ps.setString(4, rental.getEndDate().toString());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RepositoryException("Error at inserting in table Rentals. ", e);
            }
        } else {
            throw new RepositoryException("Unknown entity type.");
        }
    }

    @Override
    public void update(E element) {
        String sql;
        if (element instanceof Car car) {
            sql = "UPDATE Cars SET brand = ?, model = ? WHERE id = ?";
            try (Connection conn = connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, car.getBrand());
                ps.setString(2, car.getModel());
                ps.setInt(3, car.getId());
                int affected = ps.executeUpdate();
                if (affected == 0)
                    throw new NotFoundException("The element with ID does not exist.");
            } catch (SQLException e) {
                throw new RepositoryException("Error at updating table Cars. ", e);
            }
        } else if (element instanceof Rental rental) {
            sql = "UPDATE Rentals SET carId = ?, startDate = ?, endDate = ? WHERE id = ?";
            try (Connection conn = connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(4, rental.getId());
                ps.setInt(1, rental.getCar().getId());
                ps.setString(2, rental.getStartDate().toString());
                ps.setString(3, rental.getEndDate().toString());
                int affected = ps.executeUpdate();
                if (affected == 0)
                    throw new NotFoundException("The element with ID does not exist.");
            } catch (SQLException e) {
                throw new RepositoryException("Error at updating table Rentals. ", e);
            }
        } else {
            throw new RepositoryException("Unknown entity type.");
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM " + tableName + " WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            if (affected == 0)
                throw new NotFoundException("The element with ID " + id + " does not exist.");
        } catch (SQLException e) {
            throw new RepositoryException("Error at deleting. ", e);
        }
    }

    @Override
    public boolean exists(int id) {
        String sql = "SELECT id FROM " + tableName + " WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RepositoryException("Error at verifying existence. ", e);
        }
    }

    @Override
    public E getById(int id) {
        String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return factory.fromResultSet(rs);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error at finding by ID. ", e);
        }
    }

    @Override
    public ArrayList<E> getAll() {
        ArrayList<E> list = new ArrayList<>();
        String sql;

        if (tableName.equalsIgnoreCase("cars")) {
            sql = "SELECT id, brand, model FROM Cars";
        } else if (tableName.equalsIgnoreCase("rentals")) {
            sql = """
              SELECT r.id,
                     r.carId,
                     c.brand,
                     c.model,
                     r.startDate,
                     r.endDate
              FROM Rentals r
              JOIN Cars c ON r.carId = c.id
              """;
        } else {
            throw new RepositoryException("Unknown table type: " + tableName);
        }

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(factory.fromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error at reading all elements. ", e);
        }

        return list;
    }


    @Override
    public int size() {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new RepositoryException("Error at calculating size. ", e);
        }
    }

    @Override
    public void clear() {
        String sql = "DELETE FROM " + tableName;
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RepositoryException("Error at clear. ", e);
        }
    }
}
