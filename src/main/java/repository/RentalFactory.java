package repository;

import domain.Car;
import domain.Rental;
import exceptions.RepositoryException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;

public class RentalFactory implements InterfaceFactory<Rental> {
    private static final String DELIM = ";";

    @Override
    public Rental fromTokens(String[] tokens) {
        try {
            if (tokens == null || tokens.length < 6)
                throw new RepositoryException("Invalid line for Rental: " + Arrays.toString(tokens));

            int id = Integer.parseInt(tokens[0].trim());
            int carId = Integer.parseInt(tokens[1].trim());
            String brand = tokens[2].trim();
            String model = tokens[3].trim();
            Car car = new Car(carId, brand, model);
            String start = tokens[4].trim();
            String end = tokens[5].trim();

            return new Rental(id, car, start, end);
        } catch (NumberFormatException e) {
            throw new RepositoryException("Invalid numerical format in line: " + Arrays.toString(tokens), e);
        }
    }

    @Override
    public String toLine(Rental rental) {
        Car car = rental.getCar();
        return rental.getId() + DELIM
                + car.getId() + DELIM
                + safe(car.getBrand()) + DELIM
                + safe(car.getModel()) + DELIM
                + rental.getStartDate() + DELIM
                + rental.getEndDate();
    }

    @Override
    public Rental fromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");

        int carId = rs.getInt("carId");
        String brand = rs.getString("brand");
        String model = rs.getString("model");
        Car car = new Car(carId, brand, model);

        String startDate = rs.getString("startDate");
        String endDate = rs.getString("endDate");

        return new Rental(id, car, startDate, endDate);
    }

    private String safe(String s) {
        if (s == null) {
            return "";
        }
        s = s.replace("\n", " ");
        return s.trim();
    }
}
