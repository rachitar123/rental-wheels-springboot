package com.rentwheels.repository;

import com.rentwheels.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Integer> {

    List<Rental> findAllByOrderByIdDesc();

    List<Rental> findByStatusOrderByIdDesc(String status);

    List<Rental> findByCustomer_Id(String customerId);

    List<Rental> findByVehicle_VehicleId(String vehicleId);

    @Query("SELECT COALESCE(SUM(r.totalRent), 0.0) FROM Rental r")
    Double sumTotalRent();
}
