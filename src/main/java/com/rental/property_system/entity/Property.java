package com.rental.property_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "properties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "property_id")
    private Long id;


    // Owner relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User owner;


    @Column(nullable = false, length = 150)
    private String title;


    @Column(nullable = false, length = 255)
    private String location;


    @Column(columnDefinition = "TEXT")
    private String description;


    @Column(name = "rent_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal rentPrice;


    @Column(nullable = false, length = 20)
    private String status = "AVAILABLE";


    @Column(name = "is_active")
    private Boolean isActive = true;


    @Column(name = "area_sqft")
    private Double areaSqft;


    private Integer bedrooms;


    private Double bathrooms;


    @Column(name = "property_age")
    private Integer propertyAge;


    @Column(name = "is_furnished")
    private Boolean isFurnished = false;


    @Column(name = "has_ac")
    private Boolean hasAc = false;


    @Column(name = "parking_spots")
    private Integer parkingSpots = 0;



    // Property Images Relationship
    @OneToMany(
            mappedBy = "property",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<PropertyImage> images = new ArrayList<>();



    // Safe image setter for Hibernate
    public void setImages(List<PropertyImage> images) {

        if(this.images == null){
            this.images = new ArrayList<>();
        }

        this.images.clear();


        if(images != null){

            for(PropertyImage image : images){

                addImage(image);

            }

        }
    }



    // Add image helper method
    public void addImage(PropertyImage image){

        if(image != null){

            images.add(image);

            image.setProperty(this);

        }

    }



    // Remove image helper method
    public void removeImage(PropertyImage image){

        if(image != null){

            images.remove(image);

            image.setProperty(null);

        }

    }



    // Primary image
    public String getPrimaryImageUrl(){

        if(images != null && !images.isEmpty()){

            return images.stream()
                    .filter(image ->
                            Boolean.TRUE.equals(image.getIsPrimary()))
                    .findFirst()
                    .orElse(images.get(0))
                    .getImageUrl();

        }


        return "/images/default-property.jpg";
    }



    public boolean hasImages(){

        return images != null && !images.isEmpty();

    }

}