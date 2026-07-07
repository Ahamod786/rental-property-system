package com.rental.property_system.controller;

import com.rental.property_system.entity.Booking;
import com.rental.property_system.entity.User;
import com.rental.property_system.service.BookingService;
import com.rental.property_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;

    @GetMapping("/mine")
    public List<Booking> myBookings(Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        return bookingService.getBookingsByTenant(user.getId());
    }

    @PostMapping
    public ResponseEntity<Booking> requestBooking(@RequestParam Long propertyId,
                                                  @RequestParam String startDate,
                                                  @RequestParam String endDate,
                                                  Principal principal) {
        User tenant = userService.getUserByEmail(principal.getName());
        Booking booking = bookingService.requestBooking(propertyId, tenant.getId(),
                LocalDate.parse(startDate), LocalDate.parse(endDate));
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    @PostMapping("/{id}/approve")
    public Booking approve(@PathVariable Long id) {
        return bookingService.approveBooking(id);
    }

    @PostMapping("/{id}/reject")
    public Booking reject(@PathVariable Long id) {
        return bookingService.rejectBooking(id);
    }

    @PostMapping("/{id}/cancel")
    public Booking cancel(@PathVariable Long id, Principal principal) {
        Booking booking = bookingService.getBookingById(id);
        User user = userService.getUserByEmail(principal.getName());
        if (!booking.getTenant().getId().equals(user.getId())) {
            throw new RuntimeException("You can only cancel your own booking");
        }
        return bookingService.cancelBooking(id);
    }

    @PostMapping("/{id}/complete")
    public Booking complete(@PathVariable Long id) {
        return bookingService.completeBooking(id);
    }
}
