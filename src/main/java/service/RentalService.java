package service;

import domain.Car;
import domain.Rental;
import repository.InterfaceRepository;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RentalService {
    private final InterfaceRepository<Rental> repo;

    public RentalService(InterfaceRepository<Rental> repo) {
        this.repo = repo;
    }

    public void addRental(Rental rental) {
        repo.add(rental);
    }

    public void deleteRental(int id) {
        repo.delete(id);
    }

    public void updateRental(Rental rental) {
        repo.update(rental);
    }

    public Rental getRental(int id) {
        return repo.getById(id);
    }

    public ArrayList<Rental> getAllRentals() {
        return repo.getAll();
    }

    public List<Map.Entry<String, Long>> getMostRentedCarModels() {
        return repo.getAll().stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCar().getBrand() + " " + r.getCar().getModel(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .toList();
    }

    public List<Map.Entry<Month, Long>> getRentalsCountPerMonth() {
        return repo.getAll().stream()
                .collect(Collectors.groupingBy(
                        r -> LocalDate.parse(r.getStartDate()).getMonth(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<Month, Long>comparingByValue().reversed())
                .toList();
    }

    public List<Map.Entry<String, Long>> getTotalDaysPerCarModel() {
        return repo.getAll().stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCar().getBrand() + " " + r.getCar().getModel(),
                        Collectors.summingLong(r -> {
                            try {
                                return ChronoUnit.DAYS.between(
                                        LocalDate.parse(r.getStartDate()),
                                        LocalDate.parse(r.getEndDate())
                                );
                            } catch (Exception e) { return 0; }
                        })
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .toList();
    }
}