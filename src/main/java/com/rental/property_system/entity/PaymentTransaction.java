package com.rental.property_system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Payment_Transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    // ManyToOne: A tenant might pay the deposit first, and the rent later, meaning Many payments link to One booking
    @ManyToOne
    @JoinColumn(name = "booking_id", referencedColumnName = "booking_id")
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "tenant_id", referencedColumnName = "user_id")
    private User tenant;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @CreationTimestamp
    @Column(name = "payment_date", updatable = false)
    private LocalDateTime paymentDate;

    @Column(name = "payment_method", nullable = false, length = 30)
    private String paymentMethod;

    @Column(name = "payment_status", nullable = false, length = 20)
    private String paymentStatus;

    @Column(name = "transaction_ref", nullable = false, unique = true, length = 100)
    private String transactionRef;
}