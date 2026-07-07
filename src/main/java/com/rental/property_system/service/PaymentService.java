package com.rental.property_system.service;

import com.rental.property_system.entity.Booking;
import com.rental.property_system.entity.PaymentTransaction;
import com.rental.property_system.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentTransactionRepository paymentRepository;
    private final BookingService bookingService;

    @Transactional
    public PaymentTransaction processPayment(Long bookingId, String paymentMethod,
                                             BigDecimal amount) {
        Booking booking = bookingService.getBookingById(bookingId);

        if (!booking.getBookingStatus().equals("APPROVED")) {
            throw new RuntimeException("Only approved bookings can be paid!");
        }

        PaymentTransaction payment = new PaymentTransaction();
        payment.setBooking(booking);
        payment.setTenant(booking.getTenant());
        payment.setAmount(amount != null ? amount : booking.getTotalPrice());
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus("PAID");
        payment.setTransactionRef("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setPaymentDate(LocalDateTime.now());

        PaymentTransaction savedPayment = paymentRepository.save(payment);

        booking.setBookingStatus("COMPLETED");
        bookingService.completeBooking(bookingId);

        return savedPayment;
    }

    public List<PaymentTransaction> getPaymentsByBooking(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }

    public PaymentTransaction getPaymentByTransactionRef(String transactionRef) {
        return paymentRepository.findByTransactionRef(transactionRef)
                .orElseThrow(() -> new RuntimeException("Payment not found!"));
    }
}