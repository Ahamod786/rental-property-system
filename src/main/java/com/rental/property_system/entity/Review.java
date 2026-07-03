package com.rental.property_system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "Review")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    // ManyToOne: Many reviews can be written for One property
    @ManyToOne
    @JoinColumn(name = "property_id", referencedColumnName = "property_id")
    private Property property;

    @ManyToOne
    @JoinColumn(name = "tenant_id", referencedColumnName = "user_id")
    private User tenant;

    @Min(1)
    @Max(5)
    @Column(name = "rating", nullable = false)
    private int rating;

    // Java uses String. We tell the database "Ayo make it TEXT format".
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;
}