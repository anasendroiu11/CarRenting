package domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Car extends Entity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String brand;
    private String model;

    public Car(int id, String brand, String model) {
        super(id);
        this.brand = brand;
        this.model = model;
    }

    public String getBrand() {
        return this.brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + getId() +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                '}';
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o)
//            return true;
//        if (o == null || getClass() != o.getClass())
//            return false;
//        Car car = (Car) o;
//        return getId() == car.getId();
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(getId());
//    }
}
