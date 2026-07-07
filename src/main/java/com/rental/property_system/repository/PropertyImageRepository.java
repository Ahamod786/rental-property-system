package com.rental.property_system.repository;

import com.rental.property_system.entity.Property;
import com.rental.property_system.entity.PropertyImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PropertyImageRepository extends JpaRepository<PropertyImage, Long> {
    List<PropertyImage> findByPropertyOrderByIsPrimaryDescIdAsc(Property property);

    List<PropertyImage> findByPropertyIdOrderByIsPrimaryDescIdAsc(Long propertyId);

    Optional<PropertyImage> findFirstByPropertyAndIsPrimaryTrue(Property property);

    void deleteByProperty(Property property);
}
