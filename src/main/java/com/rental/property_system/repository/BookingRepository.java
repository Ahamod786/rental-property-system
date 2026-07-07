package com.rental.property_system.repository;

import com.rental.property_system.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Custom query to find overlapping approved bookings
    @Query("SELECT b FROM Booking b WHERE b.property.id = :propertyId AND b.bookingStatus = 'APPROVED' AND (b.startDate <= :endDate AND b.endDate >= :startDate)")
    List<Booking> findApprovedOverlappingBookings(
            @Param("propertyId") Long propertyId, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate
    );
    // Spring automatically writes the SQL to find bookings by a specific tenant
    List<Booking> findByTenant(com.rental.property_system.entity.User tenant);
    List<Booking> findByPropertyId(Long propertyId);
    List<Booking> findByPropertyIdAndBookingStatus(Long propertyId, String status);
}