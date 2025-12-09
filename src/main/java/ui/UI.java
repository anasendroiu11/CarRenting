package ui;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

import domain.Car;
import domain.Rental;
import exceptions.NotFoundException;
import exceptions.OverlappingException;
import service.CarService;
import service.RentalService;


public class UI {
    private final CarService carService;
    private final RentalService rentalService;
    private final Scanner scanner = new Scanner(System.in);

    public UI(CarService carService, RentalService rentalService) {
        this.carService = carService;
        this.rentalService = rentalService;
    }

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("1. Manage cars.");
            System.out.println("2. Manage rentals.");
            System.out.println("0. Exit.");
            System.out.print("Option: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 -> carMenu();
                case 2 -> rentalMenu();
                case 0 -> {
                    running = false;
                    System.out.println("Exit.");
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    public void carMenu() {
        boolean running = true;

        while (running) {
            System.out.println("1. Add car.");
            System.out.println("2. Delete car.");
            System.out.println("3. Update car.");
            System.out.println("4. Find car.");
            System.out.println("5. Print all cars.");
            System.out.println("0. Exit.");
            System.out.print("Option: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 -> addCar();
                case 2 -> deleteCar();
                case 3 -> updateCar();
                case 4 -> findCar();
                case 5 -> printCars();
                case 0 -> {
                    running = false;
                    System.out.println("Exit.");
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    public void rentalMenu() {
        boolean running = true;

        while (running) {
            System.out.println("1. Add rental.");
            System.out.println("2. Delete rental.");
            System.out.println("3. Update rental.");
            System.out.println("4. Find rental.");
            System.out.println("5. Print all rentals.");
            System.out.println("0. Exit.");
            System.out.print("Option: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 -> addRental();
                case 2 -> deleteRental();
                case 3 -> updateRental();
                case 4 -> findRental();
                case 5 -> printRentals();
                case 0 -> {
                    running = false;
                    System.out.println("Exit.");
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    public void addCar() {
        System.out.println("Car ID: ");
        int id = scanner.nextInt();

        System.out.print("Car brand: ");
        String brand = scanner.nextLine();

        System.out.print("Car model: ");
        String model = scanner.nextLine();

        carService.addCar(id, brand, model);
        System.out.println("Added car.\n");

    }

    public void deleteCar() {
        try {
            System.out.print("Car ID to delete: ");
            int id = scanner.nextInt();

            carService.deleteCar(id);
            System.out.println("Deleted car.\n");
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateCar() {
        try {
            System.out.print("Car ID to update: ");
            int id = scanner.nextInt();
            scanner.nextLine();

            Car existing = carService.getCar(id);
            if (existing == null) {
                System.out.println("Inexistent car.");
                return;
            }

            System.out.print("New brand: ");
            String brand = scanner.nextLine();

            System.out.print("New model: ");
            String model = scanner.nextLine();

            carService.updateCar(new Car(id, brand, model));
            System.out.println("Updated car.\n");
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
        }

    }

    public void findCar() {
        try {
            System.out.print("Car ID to find: ");
            int id = scanner.nextInt();

            System.out.println(carService.getCar(id));
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public void printCars() {
        System.out.println(carService.getAllCars());
    }

    public void addRental() {
        try {
            System.out.println("Rental ID: ");
            int id = scanner.nextInt();

            System.out.print("Car ID: ");
            int carId = scanner.nextInt();
            Car car = carService.getCar(carId);
            scanner.nextLine();

            System.out.print("Start date (yyyy-MM-dd): ");
            String startDateStr = scanner.nextLine();

            System.out.print("End date (yyyy-MM-dd): ");
            String endDateStr = scanner.nextLine();

            LocalDate.parse(startDateStr);
            LocalDate.parse(endDateStr);

            rentalService.addRental(new Rental(id, car, startDateStr, endDateStr));
            System.out.println("Added rental.\n");
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
        } catch (OverlappingException e) {
            System.out.println("Rental dates overlap with an existing rental for this car.");
        } catch (DateTimeParseException e) {
            String message = e.getMessage();
            if (message.contains("Invalid value for MonthOfYear")) {
                System.out.println("Invalid date, month must be between 01 and 12.");
            } else if (message.contains("Invalid value for DayOfMonth")) {
                System.out.println("Invalid date, day should be between 01 and 31.");
            } else {
                System.out.println("Invalid date format, it should be yyyy-MM-dd.");
            }
        }
    }

    public void deleteRental() {
        try {
            System.out.print("Rental ID to delete: ");
            int id = scanner.nextInt();
            scanner.nextLine();

            rentalService.deleteRental(id);
            System.out.println("Deleted rental.\n");
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateRental() {
        try {
            System.out.print("Rental ID to update: ");
            int rentalId = scanner.nextInt();
            scanner.nextLine();

            System.out.print("New car ID: ");
            int carId = scanner.nextInt();
            Car car = carService.getCar(carId);
            scanner.nextLine();

            System.out.print("New start date (yyyy-MM-dd): ");
            String startDateStr = scanner.nextLine();

            System.out.print("New end date (yyyy-MM-dd): ");
            String endDateStr = scanner.nextLine();

            LocalDate.parse(startDateStr);
            LocalDate.parse(endDateStr);

            rentalService.updateRental(new Rental(rentalId, car, startDateStr, endDateStr));
            System.out.println("Updated rental.\n");
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
        } catch (OverlappingException e) {
            System.out.println("Rental dates overlap with an existing rental for this car.");
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format, it should be yyyy-MM-dd.");
        }
    }

    public void findRental() {
        try {
            System.out.print("Rental ID to find: ");
            int id = scanner.nextInt();
            scanner.nextLine();

            System.out.println(rentalService.getRental(id));
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public void printRentals() {
        System.out.println(rentalService.getAllRentals());
    }

}
