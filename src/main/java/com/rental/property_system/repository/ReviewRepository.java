package com.rental.property_system.repository;

import com.rental.property_system.entity.Booking;
import com.rental.property_system.entity.Property;
import com.rental.property_system.entity.Review;
import com.rental.property_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByPropertyOrderByIdDesc(Property property);

    List<Review> findByTenantOrderByIdDesc(User tenant);

    Optional<Review> findByBooking(Booking booking);
}
