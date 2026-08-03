package com.rentwheels.config;

import com.rentwheels.entity.Customer;
import com.rentwheels.entity.User;
import com.rentwheels.entity.Vehicle;
import com.rentwheels.repository.CustomerRepository;
import com.rentwheels.repository.UserRepository;
import com.rentwheels.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String encodedPass = passwordEncoder.encode("1234");

        // Create default ADMIN only if it does not already exist
        if (userRepository.findByUsername("admin").isEmpty()) {
            userRepository.save(new User(0, "admin", encodedPass, "ADMIN", "System Administrator"));
        }

        // Create default EMPLOYEE only if it does not already exist
        if (userRepository.findByUsername("employee").isEmpty()) {
            userRepository.save(new User(0, "employee", encodedPass, "EMPLOYEE", "Standard Operator"));
        }

        // Demo customers only if the Customer table is empty
        if (customerRepository.count() == 0) {
            customerRepository.save(new Customer("CUST101", "Ali Ahmed", "0300-1234567", "ali@example.com", "AP-872A"));
            customerRepository.save(new Customer("CUST102", "Sara Khan", "0321-9876543", "sara@example.com", "PB-901B"));
            customerRepository.save(new Customer("CUST103", "John Doe", "0123-4567890", "john@example.com", "DL-342C"));
        }

        // Demo vehicles only if the Vehicle table is empty
        if (vehicleRepository.count() == 0) {
            vehicleRepository.save(new Vehicle("101", "Toyota Corolla", "Sedan", 5000.0, 1, null));
            vehicleRepository.save(new Vehicle("102", "Honda Civic", "Sedan", 5500.0, 1, null));
            vehicleRepository.save(new Vehicle("103", "Suzuki Alto", "Hatchback", 3000.0, 1, null));
            vehicleRepository.save(new Vehicle("104", "Kia Sportage", "SUV", 8000.0, 1, null));
        }
    }
}
