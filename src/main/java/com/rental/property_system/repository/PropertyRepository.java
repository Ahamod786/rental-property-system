package com.rental.property_system.repository;

import com.rental.property_system.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    
    // Fetches all active properties (used for the owner's dashboard/general lists)
    List<Property> findByIsActiveTrue();
    
    // NEW: Fetches properties that are BOTH a specific status AND haven't been deleted
    List<Property> findByStatusAndIsActiveTrue(String status);
}