package com.rental.property_system.service;

import com.rental.property_system.entity.Property;
import com.rental.property_system.entity.User;
import com.rental.property_system.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserService userService;

    public Property addProperty(Property property, Long ownerId) {
        User owner = userService.getUserById(ownerId);
        property.setOwner(owner);
        property.setStatus("AVAILABLE");
        property.setIsActive(true);
        return propertyRepository.save(property);
    }

    public List<Property> getAllProperties() {
        return propertyRepository.findByIsActiveTrue();
    }

    public List<Property> getAvailableProperties() {
        return propertyRepository.findByStatusAndIsActiveTrue("AVAILABLE");
    }

    public List<Property> getPropertiesByOwner(Long ownerId) {
        return propertyRepository.findByOwnerIdAndIsActiveTrue(ownerId);
    }

    public Property getPropertyById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));
    }

    public Property updateProperty(Long id, Property updatedProperty) {
        Property existing = getPropertyById(id);
        if (updatedProperty.getTitle() != null) existing.setTitle(updatedProperty.getTitle());
        if (updatedProperty.getLocation() != null) existing.setLocation(updatedProperty.getLocation());
        if (updatedProperty.getRentPrice() != null) existing.setRentPrice(updatedProperty.getRentPrice());
        if (updatedProperty.getStatus() != null) existing.setStatus(updatedProperty.getStatus());
        if (updatedProperty.getAreaSqft() != null) existing.setAreaSqft(updatedProperty.getAreaSqft());
        if (updatedProperty.getBedrooms() != null) existing.setBedrooms(updatedProperty.getBedrooms());
        if (updatedProperty.getBathrooms() != null) existing.setBathrooms(updatedProperty.getBathrooms());
        if (updatedProperty.getPropertyAge() != null) existing.setPropertyAge(updatedProperty.getPropertyAge());
        if (updatedProperty.getIsFurnished() != null) existing.setIsFurnished(updatedProperty.getIsFurnished());
        if (updatedProperty.getHasAc() != null) existing.setHasAc(updatedProperty.getHasAc());
        if (updatedProperty.getParkingSpots() != null) existing.setParkingSpots(updatedProperty.getParkingSpots());
        if (updatedProperty.getDescription() != null) existing.setDescription(updatedProperty.getDescription());
        return propertyRepository.save(existing);
    }

    public void deleteProperty(Long id) {
        Property property = getPropertyById(id);
        property.setIsActive(false);
        propertyRepository.save(property);
    }

    public Property changePropertyStatus(Long id, String status) {
        Property property = getPropertyById(id);
        property.setStatus(status);
        return propertyRepository.save(property);
    }
}