package com.rental.property_system.service;

import com.rental.property_system.entity.Booking;
import com.rental.property_system.entity.Property;
import com.rental.property_system.entity.User;
import com.rental.property_system.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private static final List<String> BLOCKING_STATUSES = List.of("APPROVED");

    private final BookingRepository bookingRepository;
    private final PropertyService propertyService;
    private final UserService userService;

    @Transactional
    public Booking requestBooking(Long propertyId, Long tenantId, LocalDate startDate, LocalDate endDate) {
        validateDates(startDate, endDate);
        Property property = propertyService.getPropertyById(propertyId);
        if (!"AVAILABLE".equals(property.getStatus()) || !Boolean.TRUE.equals(property.getIsActive())) {
            throw new RuntimeException("Property is not available for booking");
        }
        if (bookingRepository.existsApprovedOverlap(propertyId, startDate, endDate)) {
            throw new RuntimeException("Property is already booked for these dates");
        }

        User tenant = userService.getUserById(tenantId);
        Booking booking = new Booking();
        booking.setProperty(property);
        booking.setTenant(tenant);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setBookingStatus("PENDING");
        booking.setTotalPrice(calculateTotalPrice(property.getRentPrice(), startDate, endDate));
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking approveBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        requireStatus(booking, "PENDING");
        if (!bookingRepository.findOverlappingBookings(
                booking.getProperty().getId(),
                booking.getStartDate(),
                booking.getEndDate(),
                BLOCKING_STATUSES).isEmpty()) {
            throw new RuntimeException("Another approved booking overlaps these dates");
        }
        booking.setBookingStatus("APPROVED");
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking rejectBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        requireStatus(booking, "PENDING");
        booking.setBookingStatus("REJECTED");
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking cancelBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        if (!List.of("PENDING", "APPROVED").contains(booking.getBookingStatus())) {
            throw new RuntimeException("Only pending or approved bookings can be cancelled");
        }
        booking.setBookingStatus("CANCELLED");
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking completeBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        requireStatus(booking, "APPROVED");
        booking.setBookingStatus("COMPLETED");
        return bookingRepository.save(booking);
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
    }

    public List<Booking> getBookingsByTenant(Long tenantId) {
        return bookingRepository.findByTenantOrderByIdDesc(userService.getUserById(tenantId));
    }

    public List<Booking> getBookingsByProperty(Long propertyId) {
        return bookingRepository.findByPropertyIdOrderByIdDesc(propertyId);
    }

    public List<Booking> getBookingsForOwner(Long ownerId) {
        List<Property> properties = propertyService.getPropertiesByOwner(ownerId);
        if (properties.isEmpty()) {
            return List.of();
        }
        return bookingRepository.findByPropertyInOrderByIdDesc(properties);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new RuntimeException("Start date and end date are required");
        }
        if (!endDate.isAfter(startDate)) {
            throw new RuntimeException("End date must be after start date");
        }
        if (startDate.isBefore(LocalDate.now())) {
            throw new RuntimeException("Start date cannot be in the past");
        }
    }

    private BigDecimal calculateTotalPrice(BigDecimal monthlyRent, LocalDate startDate, LocalDate endDate) {
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        BigDecimal dailyRate = monthlyRent.divide(BigDecimal.valueOf(30), 2, RoundingMode.HALF_UP);
        return dailyRate.multiply(BigDecimal.valueOf(days)).setScale(2, RoundingMode.HALF_UP);
    }

    private void requireStatus(Booking booking, String status) {
        if (!status.equals(booking.getBookingStatus())) {
            throw new RuntimeException("Booking must be " + status + " for this action");
        }
    }
}
