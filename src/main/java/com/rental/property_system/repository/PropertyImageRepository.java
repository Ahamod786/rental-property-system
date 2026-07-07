package com.rental.property_system.repository;

import com.rental.property_system.entity.PropertyImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PropertyImageRepository extends JpaRepository<PropertyImage, Long> {
    List<PropertyImage> findByPropertyId(Long propertyId);
    PropertyImage findByPropertyIdAndIsPrimaryTrue(Long propertyId);
    void deleteByPropertyId(Long propertyId);
}