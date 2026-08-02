package com.rentwheels.service;

import com.rentwheels.entity.Customer;
import com.rentwheels.entity.Rental;
import com.rentwheels.entity.Vehicle;
import com.rentwheels.repository.CustomerRepository;
import com.rentwheels.repository.RentalRepository;
import com.rentwheels.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RentalService {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    public List<Rental> getAllRentals() {
        return rentalRepository.findAllByOrderByIdDesc();
    }

    public List<Rental> getActiveRentals() {
        return rentalRepository.findByStatusOrderByIdDesc("ACTIVE");
    }

    public Optional<Rental> getRentalById(int id) {
        return rentalRepository.findById(id);
    }

    @Transactional
    public Rental bookVehicle(String customerId, String vehicleId, String rentalDate, String expectedReturnDate, int days) throws Exception {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Selected customer not found."));

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Selected vehicle not found."));

        if (vehicle.getAvailable() != 1) {
            throw new IllegalStateException("Vehicle is currently not available for rental.");
        }

        if (rentalDate != null && expectedReturnDate != null && expectedReturnDate.compareTo(rentalDate) < 0) {
            throw new IllegalArgumentException("Expected return date cannot be before the rental date.");
        }

        if (days <= 0) {
            days = 1;
        }

        double totalRent = days * vehicle.getRentPerDay();

        Rental rental = new Rental(0, customer, vehicle, rentalDate, expectedReturnDate, days, "ACTIVE", totalRent);

        // 1. Save Rental
        Rental savedRental = rentalRepository.save(rental);

        // 2. Mark Vehicle as Rented (0)
        vehicle.setAvailable(0);
        vehicleRepository.save(vehicle);

        return savedRental;
    }

    @Transactional
    public void deleteRental(int id) throws Exception {
        if (!rentalRepository.existsById(id)) {
            throw new IllegalArgumentException("Rental record #" + id + " not found.");
        }
        rentalRepository.deleteById(id);
    }

    public double getTotalRentRevenue() {
        Double sum = rentalRepository.sumTotalRent();
        return (sum != null) ? sum : 0.0;
    }
}
