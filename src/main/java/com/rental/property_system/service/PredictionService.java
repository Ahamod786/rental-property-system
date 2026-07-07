package com.rental.property_system.service;

import com.rental.property_system.entity.PredictionHistory;
import com.rental.property_system.entity.Property;
import com.rental.property_system.repository.PredictionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PredictionService {

    private final PredictionHistoryRepository predictionHistoryRepository;

    @Transactional
    public BigDecimal predictPrice(Property property) {
        double area = value(property.getAreaSqft(), 700);
        int bedrooms = property.getBedrooms() == null ? 1 : property.getBedrooms();
        double bathrooms = value(property.getBathrooms(), 1);
        int age = property.getPropertyAge() == null ? 5 : property.getPropertyAge();

        double locationFactor = property.getLocation() != null && property.getLocation().toLowerCase().matches(".*(central|city|metro|downtown).*")
                ? 1.35
                : 1.0;
        double estimate = ((area * 18) + (bedrooms * 3500) + (bathrooms * 2200)
                + (Boolean.TRUE.equals(property.getIsFurnished()) ? 4500 : 0)
                + (Boolean.TRUE.equals(property.getHasAc()) ? 1800 : 0)
                + (value(property.getParkingSpots(), 0) * 1200))
                * locationFactor
                - (Math.min(age, 30) * 150);

        BigDecimal predicted = BigDecimal.valueOf(Math.max(estimate, 3000)).setScale(2, RoundingMode.HALF_UP);
        PredictionHistory history = new PredictionHistory();
        history.setProperty(property.getId() == null ? null : property);
        history.setAreaSqft(property.getAreaSqft());
        history.setBedrooms(property.getBedrooms());
        history.setBathrooms(property.getBathrooms());
        history.setPropertyAge(property.getPropertyAge());
        history.setLocation(property.getLocation());
        history.setPredictedPrice(predicted);
        history.setActualPrice(property.getRentPrice());
        history.setCreatedAt(LocalDateTime.now());
        predictionHistoryRepository.save(history);
        return predicted;
    }

    private double value(Number value, double fallback) {
        return value == null ? fallback : value.doubleValue();
    }
}
