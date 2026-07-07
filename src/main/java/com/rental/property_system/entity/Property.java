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

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "area_sqft")
    private Double areaSqft;

    @Column(name = "bedrooms")
    private Integer bedrooms;

    @Column(name = "bathrooms")
    private Double bathrooms;

    @Column(name = "property_age")
    private Integer propertyAge;

    @Column(name = "is_furnished")
    private Boolean isFurnished = false;

    @Column(name = "has_ac")
    private Boolean hasAc = false;

    @Column(name = "parking_spots")
    private Integer parkingSpots = 0;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}