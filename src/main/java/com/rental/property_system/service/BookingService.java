package com.rental.property_system.service;

import com.rental.property_system.entity.Booking;
import com.rental.property_system.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    public Booking requestBooking(Booking booking) {
        // 1. Check for overlapping dates
        List<Booking> overlaps = bookingRepository.findApprovedOverlappingBookings(
                booking.getProperty().getId(),
                booking.getStartDate(),
                booking.getEndDate()
        );

        // 2. If a conflict exists, stop the process and throw an error
        if (!overlaps.isEmpty()) {
            throw new RuntimeException("Sorry, this property is already booked for those dates.");
        }
        // 3. All new bookings must start as PENDING
        booking.setBookingStatus("PENDING");
        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
    
    public Booking approveBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new RuntimeException("Booking not found"));
        
        booking.setBookingStatus("APPROVED");
        return bookingRepository.save(booking);
    }

    public void cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        // Only allow cancellation if it hasn't been paid for yet
        if (booking.getBookingStatus().equals("PENDING") || booking.getBookingStatus().equals("APPROVED")) {
            booking.setBookingStatus("CANCELLED");
            bookingRepository.save(booking);
        } else {
            throw new RuntimeException("Cannot cancel a booking that is already paid.");
        }
    }
}