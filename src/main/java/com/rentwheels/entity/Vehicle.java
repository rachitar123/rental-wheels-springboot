package com.rentwheels.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @Column(name = "id")
    private String vehicleId;

    @Column(nullable = false)
    private String vehicleName;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private double rentPerDay;

    @Column(nullable = false)
    private int available; // 1 = Available, 0 = Rented, 2 = Maintenance

    private String imagePath;

    public Vehicle() {
    }

    public Vehicle(String vehicleId, String vehicleName, String category, double rentPerDay, int available, String imagePath) {
        this.vehicleId = vehicleId;
        this.vehicleName = vehicleName;
        this.category = category;
        this.rentPerDay = rentPerDay;
        this.available = available;
        this.imagePath = imagePath;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getRentPerDay() {
        return rentPerDay;
    }

    public void setRentPerDay(double rentPerDay) {
        this.rentPerDay = rentPerDay;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getStatusString() {
        switch (available) {
            case 0: return "Rented";
            case 2: return "Maintenance";
            case 1:
            default: return "Available";
        }
    }
}
