package com.rentwheels.repository;

import com.rentwheels.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {

    List<Vehicle> findAllByOrderByVehicleIdAsc();

    List<Vehicle> findByAvailableOrderByVehicleIdAsc(int available);

    @Query("SELECT v FROM Vehicle v WHERE " +
           "(:query IS NULL OR :query = '' OR LOWER(v.vehicleId) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(v.vehicleName) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:category IS NULL OR :category = 'All' OR v.category = :category) AND " +
           "(:available IS NULL OR v.available = :available) " +
           "ORDER BY v.vehicleId ASC")
    List<Vehicle> searchVehicles(@Param("query") String query,
                                 @Param("category") String category,
                                 @Param("available") Integer available);

    long countByAvailable(int available);
}
