package com.rental.property_system.repository;

import com.rental.property_system.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    List<PaymentTransaction> findByBookingId(Long bookingId);
    Optional<PaymentTransaction> findByTransactionRef(String transactionRef);
}