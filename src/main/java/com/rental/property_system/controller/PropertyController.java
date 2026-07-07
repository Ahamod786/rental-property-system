package com.rental.property_system.controller;

import com.rental.property_system.entity.Property;
import com.rental.property_system.entity.User;
import com.rental.property_system.service.PredictionService;
import com.rental.property_system.service.PropertyImageService;
import com.rental.property_system.service.PropertyService;
import com.rental.property_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;
    private final PropertyImageService propertyImageService;
    private final PredictionService predictionService;
    private final UserService userService;

    @GetMapping
    public List<Property> getAvailableProperties() {
        return propertyService.getAvailableProperties();
    }

    @GetMapping("/{id}")
    public Property getProperty(@PathVariable Long id) {
        return propertyService.getPropertyById(id);
    }

    @PostMapping
    public ResponseEntity<Property> createProperty(@RequestBody Property property, Principal principal) {
        User owner = userService.getUserByEmail(principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(propertyService.addProperty(property, owner.getId()));
    }

    @PutMapping("/{id}")
    public Property updateProperty(@PathVariable Long id, @RequestBody Property property, Principal principal) {
        assertOwner(propertyService.getPropertyById(id), userService.getUserByEmail(principal.getName()));
        return propertyService.updateProperty(id, property);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id, Principal principal) {
        assertOwner(propertyService.getPropertyById(id), userService.getUserByEmail(principal.getName()));
        propertyService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<?> uploadImage(@PathVariable Long id,
                                         @RequestParam MultipartFile image,
                                         @RequestParam(defaultValue = "false") Boolean isPrimary,
                                         Principal principal) {
        assertOwner(propertyService.getPropertyById(id), userService.getUserByEmail(principal.getName()));
        return ResponseEntity.status(HttpStatus.CREATED).body(propertyImageService.uploadImage(id, image, isPrimary));
    }

    @PostMapping("/{id}/predict")
    public BigDecimal predictPrice(@PathVariable Long id) {
        return predictionService.predictPrice(propertyService.getPropertyById(id));
    }

    private void assertOwner(Property property, User user) {
        if (!property.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("You do not have permission to manage this property");
        }
    }
}
