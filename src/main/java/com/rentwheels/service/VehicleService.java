package com.rentwheels.service;

import com.rentwheels.entity.Rental;
import com.rentwheels.entity.Vehicle;
import com.rentwheels.repository.RentalRepository;
import com.rentwheels.repository.ReturnRepository;
import com.rentwheels.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private ReturnRepository returnRepository;

    @Autowired
    private ValidationService validationService;

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAllByOrderByVehicleIdAsc();
    }

    public List<Vehicle> getAvailableVehicles() {
        return vehicleRepository.findByAvailableOrderByVehicleIdAsc(1);
    }

    public Optional<Vehicle> getVehicleById(String id) {
        return vehicleRepository.findById(id);
    }

    public List<Vehicle> searchVehicles(String query, String category, String status) {
        Integer availableFilter = null;
        if (status != null && !status.equalsIgnoreCase("All")) {
            if (status.equalsIgnoreCase("Available")) availableFilter = 1;
            else if (status.equalsIgnoreCase("Rented")) availableFilter = 0;
            else if (status.equalsIgnoreCase("Maintenance")) availableFilter = 2;
        }

        return vehicleRepository.searchVehicles(
                (query != null && !query.trim().isEmpty()) ? query.trim() : null,
                (category != null && !category.equalsIgnoreCase("All")) ? category : null,
                availableFilter
        );
    }

    @Transactional
    public void addVehicle(Vehicle vehicle, MultipartFile imageFile) throws Exception {
        if (!validationService.isValidId(vehicle.getVehicleId())) {
            throw new IllegalArgumentException("Vehicle ID must be at least 3 characters.");
        }
        if (!validationService.isVehicleIdUnique(vehicle.getVehicleId())) {
            throw new IllegalArgumentException("Vehicle ID already exists in the system.");
        }
        if (vehicle.getVehicleName() == null || vehicle.getVehicleName().trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle name is required.");
        }
        if (!validationService.isValidRent(vehicle.getRentPerDay())) {
            throw new IllegalArgumentException("Rent per day must be a positive number.");
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = saveImage(imageFile, vehicle.getVehicleId());
            vehicle.setImagePath(imagePath);
        }

        vehicleRepository.save(vehicle);
    }

    @Transactional
    public void updateVehicle(Vehicle vehicle, MultipartFile imageFile) throws Exception {
        Optional<Vehicle> existingOpt = vehicleRepository.findById(vehicle.getVehicleId());
        if (existingOpt.isEmpty()) {
            throw new IllegalArgumentException("Vehicle with ID " + vehicle.getVehicleId() + " not found.");
        }

        if (vehicle.getVehicleName() == null || vehicle.getVehicleName().trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle name is required.");
        }
        if (!validationService.isValidRent(vehicle.getRentPerDay())) {
            throw new IllegalArgumentException("Rent per day must be a positive number.");
        }

        Vehicle existing = existingOpt.get();
        existing.setVehicleName(vehicle.getVehicleName());
        existing.setCategory(vehicle.getCategory());
        existing.setRentPerDay(vehicle.getRentPerDay());
        existing.setAvailable(vehicle.getAvailable());

        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = saveImage(imageFile, vehicle.getVehicleId());
            existing.setImagePath(imagePath);
        }

        vehicleRepository.save(existing);
    }

    @Transactional
    public void deleteVehicle(String id) throws Exception {
        if (!vehicleRepository.existsById(id)) {
            throw new IllegalArgumentException("Vehicle with ID " + id + " not found.");
        }

        // Cascade like Swing SQLite ON DELETE CASCADE
        List<Rental> rentals = rentalRepository.findByVehicle_VehicleId(id);
        for (Rental rental : rentals) {
            returnRepository.findByRental_Id(rental.getId()).ifPresent(returnRepository::delete);
            rentalRepository.delete(rental);
        }

        vehicleRepository.deleteById(id);
    }

    private String saveImage(MultipartFile file, String vehicleId) throws IOException {
        File uploadDir = new File("uploads/images");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.lastIndexOf('.') > 0) {
            ext = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }

        String filename = vehicleId + "_" + System.currentTimeMillis() + ext;
        File dest = new File(uploadDir, filename);
        Files.copy(file.getInputStream(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/images/" + filename;
    }

    public long getTotalVehiclesCount() {
        return vehicleRepository.count();
    }

    public long getAvailableVehiclesCount() {
        return vehicleRepository.countByAvailable(1);
    }

    public long getRentedVehiclesCount() {
        return vehicleRepository.countByAvailable(0);
    }

    public long getMaintenanceVehiclesCount() {
        return vehicleRepository.countByAvailable(2);
    }
}
