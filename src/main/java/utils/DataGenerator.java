package utils;

import domain.Car;
import domain.Rental;
import service.CarService;
import service.RentalService;

import java.time.LocalDate;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class DataGenerator {
    private final CarService carService;
    private final RentalService rentalService;

    public DataGenerator(CarService carService, RentalService rentalService) {
        this.carService = carService;
        this.rentalService = rentalService;
    }

    public void generateIfEmpty() {
        if (carService.countCars() > 0) {
            return;
        }

        String[] brands = {"Dacia", "Toyota", "Volkswagen", "BMW", "Audi", "Mercedes", "Ford", "Renault", "Honda", "Hyundai"};
        String[] models = {"Logan", "Sandero", "Corolla", "Golf", "Passat", "X5", "Seria 3", "A4", "Q7", "Focus", "Civic", "Tucson"};
        Random random = new Random();

        for (int i = 1; i <= 100; i++) {
            String brand = brands[random.nextInt(brands.length)];
            String model = models[random.nextInt(models.length)];
            carService.addCar(new Car(i, brand, model));
        }

        int rentalsCreated = 0;
        int rentalIdCounter = 1;

        while (rentalsCreated < 100) {
            int carId = random.nextInt(100) + 1;
            Car car = carService.getCar(carId);

            long minDay = LocalDate.of(2024, 1, 1).toEpochDay();
            long maxDay = LocalDate.of(2024, 12, 30).toEpochDay();
            long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);

            LocalDate start = LocalDate.ofEpochDay(randomDay);
            LocalDate end = start.plusDays(random.nextInt(14) + 1);

            try {
                rentalService.addRental(new Rental(rentalIdCounter, car, start.toString(), end.toString()));

                rentalsCreated++;
                rentalIdCounter++;
            } catch (Exception e) {

            }
        }
    }
}