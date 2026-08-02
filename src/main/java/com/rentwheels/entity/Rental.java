package com.rentwheels.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "rentals")
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(nullable = false)
    private String rentalDate;

    @Column(nullable = false)
    private String expectedReturnDate;

    @Column(nullable = false)
    private int days;

    @Column(nullable = false)
    private String status; // ACTIVE, RETURNED, OVERDUE

    @Column(nullable = false)
    private double totalRent;

    public Rental() {
    }

    public Rental(int id, Customer customer, Vehicle vehicle, String rentalDate, String expectedReturnDate, int days, String status, double totalRent) {
        this.id = id;
        this.customer = customer;
        this.vehicle = vehicle;
        this.rentalDate = rentalDate;
        this.expectedReturnDate = expectedReturnDate;
        this.days = days;
        this.status = status;
        this.totalRent = totalRent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public String getRentalDate() {
        return rentalDate;
    }

    public void setRentalDate(String rentalDate) {
        this.rentalDate = rentalDate;
    }

    public String getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public void setExpectedReturnDate(String expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalRent() {
        return totalRent;
    }

    public void setTotalRent(double totalRent) {
        this.totalRent = totalRent;
    }

    public double calculateRent() {
        if (vehicle != null) {
            return vehicle.getRentPerDay() * days;
        }
        return totalRent;
    }
}
