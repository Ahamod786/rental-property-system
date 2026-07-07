package com.rental.property_system.repository;

import com.rental.property_system.entity.Property;
import com.rental.property_system.entity.Review;
import com.rental.property_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Fetches all reviews linked to a specific property
    List<Review> findByProperty(Property property);

    List<Review> findByTenant(User tenant);

    List<Review> findByPropertyId(Long propertyId);
}