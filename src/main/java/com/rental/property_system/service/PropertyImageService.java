package com.rental.property_system.service;

import com.rental.property_system.entity.Property;
import com.rental.property_system.entity.PropertyImage;
import com.rental.property_system.repository.PropertyImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PropertyImageService {

    private final PropertyImageRepository propertyImageRepository;
    private final PropertyService propertyService;

    @Value("${app.upload.dir:src/main/resources/static/uploads}")
    private String uploadDir;

    @Transactional
    public PropertyImage uploadImage(Long propertyId, MultipartFile file, Boolean isPrimary) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Please select an image");
        }
        Property property = propertyService.getPropertyById(propertyId);
        String original = file.getOriginalFilename() == null ? "property.jpg" : file.getOriginalFilename();
        String extension = original.contains(".") ? original.substring(original.lastIndexOf(".")) : ".jpg";
        String filename = UUID.randomUUID() + extension.toLowerCase();

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
            Files.copy(file.getInputStream(), uploadPath.resolve(filename));
        } catch (IOException ex) {
            throw new RuntimeException("Unable to store image: " + ex.getMessage());
        }

        boolean makePrimary = Boolean.TRUE.equals(isPrimary) || getImagesByProperty(propertyId).isEmpty();
        if (makePrimary) {
            clearPrimary(property);
        }

        PropertyImage image = new PropertyImage();
        image.setProperty(property);
        image.setImageUrl("/uploads/" + filename);
        image.setIsPrimary(makePrimary);
        return propertyImageRepository.save(image);
    }

    public List<PropertyImage> getImagesByProperty(Long propertyId) {
        return propertyImageRepository.findByPropertyIdOrderByIsPrimaryDescIdAsc(propertyId);
    }

    public PropertyImage getPrimaryImage(Long propertyId) {
        Property property = propertyService.getPropertyById(propertyId);
        return propertyImageRepository.findFirstByPropertyAndIsPrimaryTrue(property)
                .orElseGet(() -> getImagesByProperty(propertyId).stream().findFirst().orElse(null));
    }

    public PropertyImage getImageById(Long imageId) {
        return propertyImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));
    }

    @Transactional
    public void deleteImage(Long imageId) {
        PropertyImage image = getImageById(imageId);
        deletePhysicalFile(image.getImageUrl());
        propertyImageRepository.delete(image);
    }

    @Transactional
    public void deleteAllImagesByProperty(Long propertyId) {
        Property property = propertyService.getPropertyById(propertyId);
        propertyImageRepository.findByPropertyOrderByIsPrimaryDescIdAsc(property)
                .forEach(image -> deletePhysicalFile(image.getImageUrl()));
        propertyImageRepository.deleteByProperty(property);
    }

    private void clearPrimary(Property property) {
        List<PropertyImage> images = propertyImageRepository.findByPropertyOrderByIsPrimaryDescIdAsc(property);
        images.forEach(image -> image.setIsPrimary(false));
        propertyImageRepository.saveAll(images);
    }

    private void deletePhysicalFile(String imageUrl) {
        if (imageUrl == null || !imageUrl.startsWith("/uploads/")) {
            return;
        }
        try {
            Files.deleteIfExists(Paths.get(uploadDir).toAbsolutePath().normalize()
                    .resolve(imageUrl.replace("/uploads/", "")));
        } catch (IOException ignored) {
            // Database cleanup should still succeed if the file was already removed.
        }
    }
}
