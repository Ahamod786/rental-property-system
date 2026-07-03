package com.rental.property_system.controller;

import com.rental.property_system.entity.Property;
import com.rental.property_system.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @PostMapping("/add")
    public Property addProperty(@RequestBody Property property) {
        return propertyService.addProperty(property);
    }

    // Maps to: localhost:8080/api/properties/available
    @GetMapping("/available")
    public List<Property> getAvailableProperties() {
        return propertyService.getAvailableProperties();
    }

    // Maps to: localhost:8080/api/properties
    @GetMapping
    public List<Property> getAllProperties() {
        return propertyService.getAllProperties();
    }
}