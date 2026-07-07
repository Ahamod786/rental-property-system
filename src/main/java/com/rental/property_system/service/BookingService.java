package com.rental.property_system.service;

import com.rental.property_system.entity.Booking;
import com.rental.property_system.entity.Property;
import com.rental.property_system.entity.User;
import com.rental.property_system.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PropertyService propertyService;
    private final UserService userService;

    @Transactional
    public Booking requestBooking(Long propertyId, Long tenantId,
                                  LocalDate startDate, LocalDate endDate) {
        Property property = propertyService.getPropertyById(propertyId);
        User tenant = userService.getUserById(tenantId);

        if (!property.getStatus().equals("AVAILABLE")) {
            throw new RuntimeException("Property is not available!");
        }

        List<Booking> overlaps = bookingRepository.findApprovedOverlappingBookings(
                propertyId, startDate, endDate
        );

        if (!overlaps.isEmpty()) {
            throw new RuntimeException("Property already booked for these dates!");
        }

        Booking booking = new Booking();
        booking.setProperty(property);
        booking.setTenant(tenant);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setBookingStatus("PENDING");

        long days = ChronoUnit.DAYS.between(startDate, endDate);
        BigDecimal dailyRate = property.getRentPrice().divide(new BigDecimal(30), 2,
                java.math.RoundingMode.HALF_UP);
        BigDecimal totalPrice = dailyRate.multiply(new BigDecimal(days));
        booking.setTotalPrice(totalPrice);

        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Transactional
    public Booking approveBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        if (!booking.getBookingStatus().equals("PENDING")) {
            throw new RuntimeException("Only pending bookings can be approved!");
        }
        booking.setBookingStatus("APPROVED");
        Property property = booking.getProperty();
        property.setStatus("RENTED");
        propertyService.updateProperty(property.getId(), property);
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking rejectBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        if (!booking.getBookingStatus().equals("PENDING")) {
            throw new RuntimeException("Only pending bookings can be rejected!");
        }
        booking.setBookingStatus("REJECTED");
        return bookingRepository.save(booking);
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        if (booking.getBookingStatus().equals("PAID")) {
            throw new RuntimeException("Cannot cancel paid booking!");
        }
        if (booking.getBookingStatus().equals("PENDING") ||
                booking.getBookingStatus().equals("APPROVED")) {
            booking.setBookingStatus("CANCELLED");
            if (booking.getBookingStatus().equals("APPROVED")) {
                Property property = booking.getProperty();
                property.setStatus("AVAILABLE");
                propertyService.updateProperty(property.getId(), property);
            }
            bookingRepository.save(booking);
        } else {
            throw new RuntimeException("Cannot cancel booking with status: " +
                    booking.getBookingStatus());
        }
    }

    @Transactional
    public Booking completeBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        if (!booking.getBookingStatus().equals("APPROVED")) {
            throw new RuntimeException("Only approved bookings can be completed!");
        }
        booking.setBookingStatus("COMPLETED");
        Property property = booking.getProperty();
        property.setStatus("AVAILABLE");
        propertyService.updateProperty(property.getId(), property);
        return bookingRepository.save(booking);
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
    }

    public List<Booking> getBookingsByTenant(Long tenantId) {
        User tenant = userService.getUserById(tenantId);
        return bookingRepository.findByTenant(tenant);
    }

    public List<Booking> getBookingsByProperty(Long propertyId) {
        return bookingRepository.findByPropertyId(propertyId);
    }

    public Booking requestBooking(Booking booking) {
        return booking;
    }
}