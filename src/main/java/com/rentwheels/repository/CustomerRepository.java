package com.rentwheels.repository;

import com.rentwheels.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    List<Customer> findAllByOrderByNameAsc();

    @Query("SELECT c FROM Customer c WHERE LOWER(c.id) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.phone) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.email) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY c.name ASC")
    List<Customer> searchCustomers(@Param("query") String query);
}
