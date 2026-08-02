package com.rentwheels.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "customers")
public class Customer extends Person {

    private String phone;
    private String email;
    private String driverLicense;

    public Customer() {
        super();
    }

    public Customer(String id, String name, String phone, String email, String driverLicense) {
        super(id, name);
        this.phone = phone;
        this.email = email;
        this.driverLicense = driverLicense;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDriverLicense() {
        return driverLicense;
    }

    public void setDriverLicense(String driverLicense) {
        this.driverLicense = driverLicense;
    }

    @Override
    public void displayInfo() {
        System.out.println(id + " | " + name + " | " + phone + " | " + email + " | " + driverLicense);
    }
}
