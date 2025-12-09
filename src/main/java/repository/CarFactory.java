package repository;

import domain.Car;
import exceptions.RepositoryException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class CarFactory implements InterfaceFactory<Car> {
    private static final String DELIM = ";";

    @Override
    public Car fromTokens(String[] tokens) {
        try {
            if (tokens == null || tokens.length < 3)
                throw new RepositoryException("Invalid line for Car: " + Arrays.toString(tokens));

            int id = Integer.parseInt(tokens[0].trim());
            String brand = tokens[1].trim();
            String model = tokens[2].trim();

            return new Car(id, brand, model);
        } catch (NumberFormatException e) {
            throw new RepositoryException("Invalid numerical format in line: " + Arrays.toString(tokens), e);
        }
    }

    @Override
    public String toLine(Car car) {
        return car.getId() + DELIM
                + safe(car.getBrand()) + DELIM
                + safe(car.getModel());
    }

    @Override
    public Car fromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String brand = rs.getString("brand");
        String model = rs.getString("model");
        return new Car(id, brand, model);
    }

    private String safe(String s) {
        if (s == null)
            return "";
        s = s.replace("\n", " ");
        return s.trim();
    }
}
