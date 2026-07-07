package com.rental.property_system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Prediction_History")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prediction_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "property_id", referencedColumnName = "property_id")
    private Property property;

    @Column(name = "area_sqft")
    private Double areaSqft;

    @Column(name = "bedrooms")
    private Integer bedrooms;

    @Column(name = "bathrooms")
    private Double bathrooms;

    @Column(name = "property_age")
    private Integer propertyAge;

    @Column(name = "location")
    private String location;

    @Column(name = "predicted_price", precision = 10, scale = 2)
    private BigDecimal predictedPrice;

    @Column(name = "actual_price", precision = 10, scale = 2)
    private BigDecimal actualPrice;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}