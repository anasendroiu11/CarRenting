package ui.gui;

import domain.Car;
import domain.Rental;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import service.CarService;
import service.RentalService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HelloController {

    @FXML private TableView<Car> carTable;
    @FXML private TableColumn<Car, Integer> colCarId;
    @FXML private TableColumn<Car, String> colCarBrand;
    @FXML private TableColumn<Car, String> colCarModel;
    @FXML private TextField txtCarId, txtCarBrand, txtCarModel, filterField;

    @FXML private TableView<Rental> rentalTable;
    @FXML private TableColumn<Rental, Integer> colRentId;
    @FXML private TableColumn<Rental, String> colRentCar;
    @FXML private TableColumn<Rental, String> colRentStart;
    @FXML private TableColumn<Rental, String> colRentEnd;

    @FXML private TextField txtRentId, txtRentCarId, txtRentStart, txtRentEnd;

    @FXML private ListView<String> listReportMostRented, listReportMonths, listReportDays;

    private CarService carService;
    private RentalService rentalService;
    private final ObservableList<Car> carList = FXCollections.observableArrayList();
    private final ObservableList<Rental> rentalList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        this.carService = HelloApplication.getCarService();
        this.rentalService = HelloApplication.getRentalService();

        setupTables();
        setupListeners();

        if (carService != null && rentalService != null) {
            refreshAll();
        }
        carTable.setItems(carList);
        rentalTable.setItems(rentalList);
    }

    private void setupTables() {
        colCarId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getId()).asObject());
        colCarBrand.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getBrand()));
        colCarModel.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getModel()));

        colRentId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getId()).asObject());
        colRentCar.setCellValueFactory(d -> {
            Car c = d.getValue().getCar();
            return new SimpleStringProperty(c.getBrand() + " " + c.getModel());
        });
        colRentStart.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStartDate()));
        colRentEnd.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEndDate()));
    }

    private void setupListeners() {
        carTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtCarId.setText(String.valueOf(newVal.getId()));
                txtCarBrand.setText(newVal.getBrand());
                txtCarModel.setText(newVal.getModel());
            }
        });

        rentalTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtRentId.setText(String.valueOf(newVal.getId()));
                txtRentCarId.setText(String.valueOf(newVal.getCar().getId()));
                txtRentStart.setText(newVal.getStartDate());
                txtRentEnd.setText(newVal.getEndDate());
            }
        });
    }

    @FXML
    protected void onAddCar() {
        try {
            carService.addCar(new Car(Integer.parseInt(txtCarId.getText()), txtCarBrand.getText(), txtCarModel.getText()));
            refreshAll();
            onClearFields();
        } catch (Exception e) {
            showAlert("Error Adding Car", e.getMessage());
        }
    }

    @FXML
    protected void onUpdateCar() {
        try {
            carService.updateCar(new Car(Integer.parseInt(txtCarId.getText()), txtCarBrand.getText(), txtCarModel.getText()));
            refreshAll();
            onClearFields();
        } catch (Exception e) {
            showAlert("Error Updating Car", e.getMessage());
        }
    }

    @FXML
    protected void onDeleteCar() {
        Car selected = carTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a car!");
            return;
        }

        try {
            List<Rental> rentalsToDelete = rentalService.getAllRentals().stream()
                    .filter(r -> r.getCar().getId() == selected.getId())
                    .toList();

            for(Rental r : rentalsToDelete) {
                rentalService.deleteRental(r.getId());
            }

            carService.deleteCar(selected.getId());

            refreshAll();
            onClearFields();
        } catch (Exception e) {
            showAlert("Error Deleting", e.getMessage());
        }
    }

    @FXML
    protected void onAddRental() {
        handleRentalOperation("ADD");
    }

    @FXML
    protected void onUpdateRental() {
        handleRentalOperation("UPDATE");
    }

    @FXML
    protected void onDeleteRental() {
        Rental selected = rentalTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a rental!");
            return;
        }

        try {
            rentalService.deleteRental(selected.getId());
            refreshRentals();
            refreshReports();
            onClearRentFields();
        } catch (Exception e) {
            showAlert("Error Deleting Rental", e.getMessage());
        }
    }

    private void handleRentalOperation(String type) {
        try {
            int rId = Integer.parseInt(txtRentId.getText());
            int cId = Integer.parseInt(txtRentCarId.getText());
            String sStart = txtRentStart.getText().trim();
            String sEnd = txtRentEnd.getText().trim();

            validateDate(sStart);
            validateDate(sEnd);

            Car car = carService.getCar(cId);
            if (car == null) throw new Exception("Car with ID " + cId + " does not exist.");

            Rental rental = new Rental(rId, car, sStart, sEnd);

            if (type.equals("ADD")) {
                rentalService.addRental(rental);
            } else {
                rentalService.updateRental(rental);
            }

            refreshRentals();
            refreshReports();
            onClearRentFields();

        } catch (DateTimeParseException dtpe) {
            showAlert("Invalid Date Format", "Please use the format: YYYY-MM-DD\nExample: 2024-05-20");
        } catch (NumberFormatException nfe) {
            showAlert("Number Error", "IDs must be valid integers.");
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    private void validateDate(String dateStr) {
        LocalDate.parse(dateStr);
    }

    @FXML
    protected void onFilterCars() {
        String filter = filterField.getText().toLowerCase();
        carList.setAll(carService.getAllCars().stream()
                .filter(c -> c.getBrand().toLowerCase().contains(filter))
                .collect(Collectors.toList()));
    }

    @FXML
    protected void onResetCars() {
        filterField.clear();
        refreshCars();
    }

    @FXML
    protected void onClearFields() {
        txtCarId.clear();
        txtCarBrand.clear();
        txtCarModel.clear();
        carTable.getSelectionModel().clearSelection();
    }

    @FXML
    protected void onClearRentFields() {
        txtRentId.clear();
        txtRentCarId.clear();
        txtRentStart.clear();
        txtRentEnd.clear();
        rentalTable.getSelectionModel().clearSelection();
    }

    private void refreshAll() {
        refreshCars();
        refreshRentals();
        refreshReports();
    }

    private void refreshCars() {
        carList.setAll(carService.getAllCars());
    }

    private void refreshRentals() {
        rentalList.setAll(rentalService.getAllRentals());
    }

    private void refreshReports() {
        ObservableList<String> rep1 = FXCollections.observableArrayList();
        rentalService.getMostRentedCarModels().forEach(e ->
                rep1.add(e.getKey() + " - Rentals: " + e.getValue())
        );
        listReportMostRented.setItems(rep1);

        ObservableList<String> rep2 = FXCollections.observableArrayList();
        rentalService.getRentalsCountPerMonth().forEach(e ->
                rep2.add(e.getKey() + ": " + e.getValue())
        );
        listReportMonths.setItems(rep2);

        ObservableList<String> rep3 = FXCollections.observableArrayList();
        rentalService.getTotalDaysPerCarModel().forEach(e ->
                rep3.add(e.getKey() + " - Total Days: " + e.getValue())
        );
        listReportDays.setItems(rep3);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}