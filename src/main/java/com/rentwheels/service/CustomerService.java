package com.rentwheels.service;

import com.rentwheels.entity.Customer;
import com.rentwheels.entity.Rental;
import com.rentwheels.entity.Vehicle;
import com.rentwheels.repository.CustomerRepository;
import com.rentwheels.repository.RentalRepository;
import com.rentwheels.repository.ReturnRepository;
import com.rentwheels.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private ReturnRepository returnRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ValidationService validationService;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAllByOrderByNameAsc();
    }

    public Optional<Customer> getCustomerById(String id) {
        return customerRepository.findById(id);
    }

    public List<Customer> searchCustomers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllCustomers();
        }
        return customerRepository.searchCustomers(query.trim());
    }

    @Transactional
    public void addCustomer(Customer customer) throws Exception {
        if (!validationService.isValidId(customer.getId())) {
            throw new IllegalArgumentException("Customer ID must be at least 3 characters.");
        }
        if (!validationService.isCustomerIdUnique(customer.getId())) {
            throw new IllegalArgumentException("Customer ID already exists in the system.");
        }
        if (!validationService.isValidName(customer.getName())) {
            throw new IllegalArgumentException("Please enter a valid customer name.");
        }
        if (!validationService.isValidPhone(customer.getPhone())) {
            throw new IllegalArgumentException("Please enter a valid phone number (7-15 digits).");
        }
        if (!validationService.isValidEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Please enter a valid email address.");
        }
        if (customer.getDriverLicense() == null || customer.getDriverLicense().trim().isEmpty()) {
            throw new IllegalArgumentException("Driving license number is required.");
        }

        customerRepository.save(customer);
    }

    @Transactional
    public void updateCustomer(Customer customer) throws Exception {
        if (!customerRepository.existsById(customer.getId())) {
            throw new IllegalArgumentException("Customer with ID " + customer.getId() + " not found.");
        }
        if (!validationService.isValidName(customer.getName())) {
            throw new IllegalArgumentException("Please enter a valid customer name.");
        }
        if (!validationService.isValidPhone(customer.getPhone())) {
            throw new IllegalArgumentException("Please enter a valid phone number.");
        }
        if (!validationService.isValidEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Please enter a valid email address.");
        }
        if (customer.getDriverLicense() == null || customer.getDriverLicense().trim().isEmpty()) {
            throw new IllegalArgumentException("Driving license number is required.");
        }

        customerRepository.save(customer);
    }

    @Transactional
    public void deleteCustomer(String id) throws Exception {
        if (!customerRepository.existsById(id)) {
            throw new IllegalArgumentException("Customer with ID " + id + " not found.");
        }

        // Cascade like Swing SQLite ON DELETE CASCADE
        List<Rental> rentals = rentalRepository.findByCustomer_Id(id);
        for (Rental rental : rentals) {
            returnRepository.findByRental_Id(rental.getId()).ifPresent(returnRepository::delete);
            if ("ACTIVE".equalsIgnoreCase(rental.getStatus()) && rental.getVehicle() != null) {
                Vehicle vehicle = rental.getVehicle();
                vehicle.setAvailable(1);
                vehicleRepository.save(vehicle);
            }
            rentalRepository.delete(rental);
        }

        customerRepository.deleteById(id);
    }

    public long getTotalCustomersCount() {
        return customerRepository.count();
    }
}
