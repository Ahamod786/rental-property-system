package com.rental.property_system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "Property")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "property_id")
    private Long id;

    // Foreign Key linking to the User table
    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "user_id")
    private User owner;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "location", nullable = false, length = 255)
    private String location;

    @Column(name = "rent_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal rentPrice;

    @Column(name = "status", nullable = false, length = 20)
    private String status; // AVAILABLE, RENTED

    private boolean isActive = true;
}