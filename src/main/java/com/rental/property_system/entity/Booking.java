package com.rental.property_system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "Booking")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long id;

    // Fixed: Many bookings can happen for One property over time
    @ManyToOne
    @JoinColumn(name = "property_id", referencedColumnName = "property_id")
    private Property property;

    // Correct: Many bookings can be made by One tenant
    @ManyToOne
    @JoinColumn(name = "tenant_id", referencedColumnName = "user_id")
    private User tenant;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "booking_status", nullable = false, length = 20)
    private String bookingStatus;
}