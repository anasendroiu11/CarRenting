package ui.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.CarService;
import service.RentalService;

import java.io.IOException;

public class HelloApplication extends Application {

    private static CarService carService;
    private static RentalService rentalService;

    public static void setServices(CarService cs, RentalService rs) {
        carService = cs;
        rentalService = rs;
    }

    public static CarService getCarService() {
        return carService;
    }

    public static RentalService getRentalService() {
        return rentalService;
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 950, 700);
        stage.setTitle("Car Renting");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}