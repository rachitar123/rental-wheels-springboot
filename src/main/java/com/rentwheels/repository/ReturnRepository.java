package com.rentwheels.repository;

import com.rentwheels.entity.Return;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReturnRepository extends JpaRepository<Return, Integer> {

    List<Return> findAllByOrderByIdDesc();

    Optional<Return> findByRental_Id(int rentalId);

    @Query("SELECT COALESCE(SUM(r.fine), 0.0) FROM Return r")
    Double sumTotalFine();
}
