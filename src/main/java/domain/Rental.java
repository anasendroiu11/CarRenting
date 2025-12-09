package domain;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

public class Rental extends Entity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Car car;
    private String startDate;
    private String endDate;

    public Rental(int id, Car car, String startDate, String endDate) {
        super(id);
        this.car = car;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Car getCar() {
        return this.car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public String getStartDate() {
        return this.startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return this.endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Rental{" +
                "id=" +getId() +
                ", car=" + car +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
