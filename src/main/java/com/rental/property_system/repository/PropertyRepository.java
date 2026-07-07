package com.rental.property_system.repository;

import com.rental.property_system.entity.Property;
import com.rental.property_system.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    @EntityGraph(attributePaths = "images")
    List<Property> findByIsActiveTrueOrderByIdDesc();

    @EntityGraph(attributePaths = "images")
    List<Property> findByStatusAndIsActiveTrueOrderByIdDesc(String status);

    @EntityGraph(attributePaths = "images")
    List<Property> findByOwnerAndIsActiveTrueOrderByIdDesc(User owner);

    @EntityGraph(attributePaths = "images")
    Optional<Property> findWithImagesById(Long id);
}
