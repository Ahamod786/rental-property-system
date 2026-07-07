package com.rental.property_system.service;

import com.rental.property_system.entity.Booking;
import com.rental.property_system.entity.PaymentTransaction;
import com.rental.property_system.entity.User;
import com.rental.property_system.repository.BookingRepository;
import com.rental.property_system.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final UserService userService;

    @Transactional
    public PaymentTransaction processPayment(Long bookingId, Long tenantId, String paymentMethod) {
        Booking booking = bookingService.getBookingById(bookingId);
        User tenant = userService.getUserById(tenantId);
        if (!booking.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("You can only pay for your own booking");
        }
        if (!"APPROVED".equals(booking.getBookingStatus())) {
            throw new RuntimeException("Only approved bookings can be paid");
        }
        if ("SUCCESS".equals(booking.getPaymentStatus()) || "PAID".equals(booking.getBookingStatus())) {
            throw new RuntimeException("This booking is already paid");
        }
        paymentTransactionRepository.findFirstByBookingAndPaymentStatus(booking, "SUCCESS")
                .ifPresent(existing -> {
                    throw new RuntimeException("This booking is already paid");
                });

        LocalDateTime paidAt = LocalDateTime.now();
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setBooking(booking);
        transaction.setTenant(tenant);
        transaction.setAmount(booking.getTotalPrice());
        transaction.setPaymentDate(paidAt);
        transaction.setPaymentMethod(paymentMethod == null || paymentMethod.isBlank() ? "CARD" : paymentMethod);
        transaction.setPaymentStatus("SUCCESS");
        transaction.setTransactionRef("MOCK-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());

        booking.setBookingStatus("PAID");
        booking.setPaymentStatus("SUCCESS");
        booking.setPaymentDate(paidAt);
        booking.getProperty().setStatus("UNAVAILABLE");
        bookingRepository.save(booking);

        return paymentTransactionRepository.save(transaction);
    }

    public PaymentTransaction getSuccessfulPaymentForBooking(Long bookingId, Long tenantId) {
        Booking booking = bookingService.getBookingById(bookingId);
        if (!booking.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("You can only view your own payment receipt");
        }
        return paymentTransactionRepository.findFirstByBookingAndPaymentStatus(booking, "SUCCESS")
                .orElseThrow(() -> new RuntimeException("Successful payment not found for this booking"));
    }

    public PaymentTransaction getLatestPaymentForBooking(Long bookingId, Long tenantId) {
        Booking booking = bookingService.getBookingById(bookingId);
        if (!booking.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("You can only view your own payment receipt");
        }
        return paymentTransactionRepository.findFirstByBookingOrderByPaymentDateDesc(booking)
                .orElseThrow(() -> new RuntimeException("Payment not found for this booking"));
    }
}
