package com.rental.property_system.service;

import com.rental.property_system.entity.Property;
import com.rental.property_system.entity.PropertyImage;
import com.rental.property_system.repository.PropertyImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyImageService {

    private final PropertyImageRepository propertyImageRepository;
    private final PropertyService propertyService;

    public PropertyImage addImage(Long propertyId, String imageUrl, Boolean isPrimary) {
        Property property = propertyService.getPropertyById(propertyId);

        if (isPrimary != null && isPrimary) {
            // If this is primary, remove primary status from other images
            PropertyImage existingPrimary = propertyImageRepository
                    .findByPropertyIdAndIsPrimaryTrue(propertyId);
            if (existingPrimary != null) {
                existingPrimary.setIsPrimary(false);
                propertyImageRepository.save(existingPrimary);
            }
        }

        PropertyImage image = new PropertyImage();
        image.setProperty(property);
        image.setImageUrl(imageUrl);
        image.setIsPrimary(isPrimary != null ? isPrimary : false);

        return propertyImageRepository.save(image);
    }

    public List<PropertyImage> getImagesByProperty(Long propertyId) {
        return propertyImageRepository.findByPropertyId(propertyId);
    }

    public PropertyImage getPrimaryImage(Long propertyId) {
        return propertyImageRepository.findByPropertyIdAndIsPrimaryTrue(propertyId);
    }

    public void deleteImage(Long imageId) {
        propertyImageRepository.deleteById(imageId);
    }

    public void deleteAllImagesByProperty(Long propertyId) {
        propertyImageRepository.deleteByPropertyId(propertyId);
    }
}