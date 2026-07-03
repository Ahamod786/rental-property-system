package com.rental.property_system.service;

import com.rental.property_system.entity.Booking;
import com.rental.property_system.entity.PaymentTransaction;
import com.rental.property_system.repository.BookingRepository;
import com.rental.property_system.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentTransactionRepository paymentRepository;
    private final BookingRepository bookingRepository;

    // @Transactional ensures full ACID compliance for the database
    @Transactional
    public void processPayment(Long bookingId, String paymentMethod) {
        // 1. Fetch the approved booking
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // 2. Create the Payment Record
        PaymentTransaction payment = new PaymentTransaction();
        payment.setBooking(booking);
        payment.setTenant(booking.getTenant());
        payment.setAmount(booking.getProperty().getRentPrice());
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus("SUCCESS");
        
        // Generate a random unique transaction reference number
        payment.setTransactionRef("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        paymentRepository.save(payment);

        // 3. Update the Booking Status
        booking.setBookingStatus("PAID");
        bookingRepository.save(booking);
    }
}