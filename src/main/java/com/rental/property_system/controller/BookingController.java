package com.rental.property_system.controller;

import com.rental.property_system.entity.Booking;
import com.rental.property_system.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/request")
    public Booking requestBooking(@RequestBody Booking booking) {
        return bookingService.requestBooking(booking);
    }

    // PUT request to update an existing record.
    // Example URL: localhost:8080/api/bookings/1/approve
    @PutMapping("/{id}/approve")
    public Booking approveBooking(@PathVariable Long id) {
        return bookingService.approveBooking(id);
    }

    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }
}