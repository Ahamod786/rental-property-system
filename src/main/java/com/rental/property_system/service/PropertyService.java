package com.rental.property_system.service;

import com.rental.property_system.entity.Property;
import com.rental.property_system.entity.User;
import com.rental.property_system.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserService userService;
    private PropertyImageService propertyImageService;

    @Autowired
    public void setPropertyImageService(@Lazy PropertyImageService propertyImageService) {
        this.propertyImageService = propertyImageService;
    }

    @Transactional
    public Property addProperty(Property property, Long ownerId) {
        User owner = userService.getUserById(ownerId);
        property.setOwner(owner);
        applyDefaults(property);
        return propertyRepository.save(property);
    }

    @Transactional
    public Property updateProperty(Long propertyId, Property updated) {
        Property property = getPropertyById(propertyId);
        property.setTitle(updated.getTitle());
        property.setLocation(updated.getLocation());
        property.setDescription(updated.getDescription());
        property.setRentPrice(updated.getRentPrice());
        property.setStatus(updated.getStatus());
        property.setAreaSqft(updated.getAreaSqft());
        property.setBedrooms(updated.getBedrooms());
        property.setBathrooms(updated.getBathrooms());
        property.setPropertyAge(updated.getPropertyAge());
        property.setIsFurnished(Boolean.TRUE.equals(updated.getIsFurnished()));
        property.setHasAc(Boolean.TRUE.equals(updated.getHasAc()));
        property.setParkingSpots(updated.getParkingSpots() == null ? 0 : updated.getParkingSpots());
        applyDefaults(property);
        return propertyRepository.save(property);
    }

    @Transactional
    public void deleteProperty(Long id) {
        Property property = getPropertyById(id);
        property.setIsActive(false);
        property.setStatus("UNAVAILABLE");
        propertyRepository.save(property);
    }

    public List<Property> getAllProperties() {
        return propertyRepository.findByIsActiveTrueOrderByIdDesc();
    }

    public List<Property> getAvailableProperties() {
        return propertyRepository.findByStatusAndIsActiveTrueOrderByIdDesc("AVAILABLE");
    }

    public List<Property> getPropertiesByOwner(Long ownerId) {
        return propertyRepository.findByOwnerAndIsActiveTrueOrderByIdDesc(userService.getUserById(ownerId));
    }

    public Property getPropertyById(Long id) {
        Property property = propertyRepository.findWithImagesById(id)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));
        if (propertyImageService != null) {
            property.setImages(propertyImageService.getImagesByProperty(id));
        }
        return property;
    }

    private void applyDefaults(Property property) {
        if (property.getStatus() == null || property.getStatus().isBlank()) {
            property.setStatus("AVAILABLE");
        }
        if (property.getRentPrice() == null || property.getRentPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Rent price must be greater than zero");
        }
        property.setIsActive(property.getIsActive() == null || property.getIsActive());
        property.setIsFurnished(Boolean.TRUE.equals(property.getIsFurnished()));
        property.setHasAc(Boolean.TRUE.equals(property.getHasAc()));
        property.setParkingSpots(property.getParkingSpots() == null ? 0 : property.getParkingSpots());
    }
}
