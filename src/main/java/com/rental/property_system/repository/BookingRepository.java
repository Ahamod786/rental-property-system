package com.rental.property_system.repository;

import com.rental.property_system.entity.Booking;
import com.rental.property_system.entity.Property;
import com.rental.property_system.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @EntityGraph(attributePaths = {"property", "tenant", "property.images"})
    List<Booking> findByTenantOrderByIdDesc(User tenant);

    @EntityGraph(attributePaths = {"property", "tenant", "property.images"})
    List<Booking> findByPropertyInOrderByIdDesc(Collection<Property> properties);

    @EntityGraph(attributePaths = {"property", "tenant", "property.images"})
    List<Booking> findByPropertyIdOrderByIdDesc(Long propertyId);

    @Query("""
            select b from Booking b
            where b.property.id = :propertyId
              and b.bookingStatus in :statuses
              and b.startDate < :endDate
              and b.endDate > :startDate
            """)
    List<Booking> findOverlappingBookings(@Param("propertyId") Long propertyId,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate,
                                          @Param("statuses") Collection<String> statuses);

    @Query("""
            select count(b) > 0 from Booking b
            where b.property.id = :propertyId
              and b.bookingStatus = 'APPROVED'
              and b.startDate < :endDate
              and b.endDate > :startDate
            """)
    boolean existsApprovedOverlap(@Param("propertyId") Long propertyId,
                                  @Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate);
}
