package service;

import domain.Car;
import repository.InterfaceRepository;

import java.util.ArrayList;

public class CarService {
    private final InterfaceRepository<Car> repo;

    public CarService(InterfaceRepository<Car> repo) {
        this.repo = repo;
    }

    public Car addCar(int id, String brand, String model) {
        Car car = new Car(id, brand, model);
        repo.add(car);
        return car;
    }

    public Car addCar(Car car) {
        repo.add(car);
        return car;
    }

    public void deleteCar(int id) {
        repo.delete(id);
    }

    public void updateCar(Car car) {
        repo.update(car);
    }

    public Car getCar(int id) {
        return repo.getById(id);
    }

    public ArrayList<Car> getAllCars() {
        return repo.getAll();
    }

    public int countCars() {
        return repo.size();
    }
}
