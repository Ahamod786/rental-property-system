package com.rental.property_system.repository;

import com.rental.property_system.entity.Booking;
import com.rental.property_system.entity.PaymentTransaction;
import com.rental.property_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    Optional<PaymentTransaction> findFirstByBookingAndPaymentStatus(Booking booking, String paymentStatus);

    List<PaymentTransaction> findByTenantOrderByPaymentDateDesc(User tenant);
}
