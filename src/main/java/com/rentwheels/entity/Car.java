package com.rentwheels.entity;

public class Car extends Vehicle {

    public Car() {
        super();
    }

    public Car(String vehicleId, String vehicleName, double rentPerDay) {
        super(vehicleId, vehicleName, "Car", rentPerDay, 1, null);
    }
}
