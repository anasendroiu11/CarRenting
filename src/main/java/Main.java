import repository.*;
import service.Settings;
import ui.UI;
import ui.gui.HelloApplication;
import javafx.application.Application;
import domain.Car;
import domain.Rental;
import service.CarService;
import service.RentalService;
import utils.DataGenerator;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        Settings settings = Settings.getInstance();

        String repoType = settings.getRepositoryType();
        String uiType = settings.getUIType();

        System.out.println("Repository type: " + repoType);
        System.out.println("UI type: " + uiType);

        InterfaceRepository<Car> carRepository = switch (repoType) {
            case "memory" -> new InMemoryRepository<>();
            case "binary" -> new BinaryFileRepository<>(Path.of(settings.getCarsPath()));
            case "text" -> new TextFileRepository<>(Path.of(settings.getCarsPath()), new CarFactory());
            case "sql" -> new SQLRepository<>(settings.getDatabasePath(), new CarFactory(), "Cars");
            default -> throw new IllegalArgumentException("Unknown repository type: " + repoType);
        };

        InterfaceRepository<Rental> rentalRepository = switch (repoType) {
            case "memory" -> new InMemoryRepository<>();
            case "binary" -> new BinaryFileRepository<>(Path.of(settings.getRentalsPath()));
            case "text" -> new TextFileRepository<>(Path.of(settings.getRentalsPath()), new RentalFactory());
            case "sql" -> new SQLRepository<>(settings.getDatabasePath(), new RentalFactory(), "Rentals");
            default -> throw new IllegalArgumentException("Unknown repository type: " + repoType);
        };

        CarService carService = new CarService(carRepository);
        RentalService rentalService = new RentalService(rentalRepository);

        DataGenerator generator = new DataGenerator(carService, rentalService);
        generator.generateIfEmpty();

        if ("GUI".equalsIgnoreCase(uiType)) {
            HelloApplication.setServices(carService, rentalService);
            Application.launch(HelloApplication.class, args);
        } else {
            UI ui = new UI(carService, rentalService);
            ui.start();
        }
    }
}