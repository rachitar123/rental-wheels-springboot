package com.rentwheels.service;

import com.rentwheels.repository.CustomerRepository;
import com.rentwheels.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class ValidationService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?[0-9\\-\\s]{7,15}$"
    );

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return true; // Optional email
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    public boolean isValidId(String id) {
        return id != null && !id.trim().isEmpty() && id.trim().length() >= 3;
    }

    public boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.trim().length() >= 2;
    }

    public boolean isCustomerIdUnique(String id) {
        return !customerRepository.existsById(id);
    }

    public boolean isVehicleIdUnique(String id) {
        return !vehicleRepository.existsById(id);
    }

    public boolean isValidRent(double rent) {
        return rent > 0.0;
    }

    public boolean isValidDays(int days) {
        return days > 0;
    }
}
