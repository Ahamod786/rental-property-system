package com.rental.property_system.repository;

import com.rental.property_system.entity.PredictionHistory;
import com.rental.property_system.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PredictionHistoryRepository extends JpaRepository<PredictionHistory, Long> {
    List<PredictionHistory> findByPropertyOrderByCreatedAtDesc(Property property);
}
