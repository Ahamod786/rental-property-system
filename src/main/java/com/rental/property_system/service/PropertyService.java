package com.rental.property_system.service;

import com.rental.property_system.entity.Property;
import com.rental.property_system.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public Property addProperty(Property property) {
        // By default, a new property is available
        property.setStatus("AVAILABLE");
        return propertyRepository.save(property);
    }

    public List<Property> getAvailableProperties() {
        // It checks that it's available AND not soft-deleted
        return propertyRepository.findByStatusAndIsActiveTrue("AVAILABLE");
    }

    public List<Property> getAllProperties() {
        return propertyRepository.findByIsActiveTrue();
    }

    public Property getPropertyById(Long id) {
        // Fetches a property by its ID, or throws an error if it doesn't exist
        return propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
    }

    public void deleteProperty(Long id) {
        Property property = getPropertyById(id);
        property.setActive(false); // This hides it from the public!
        propertyRepository.save(property);
    }
}