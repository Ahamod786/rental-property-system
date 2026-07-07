package com.rental.property_system.service;

import com.rental.property_system.entity.PredictionHistory;
import com.rental.property_system.entity.Property;
import com.rental.property_system.repository.PredictionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PredictionService {

    private final PredictionHistoryRepository predictionRepository;
    private final PropertyService propertyService;

    public BigDecimal predictRent(Property property) {
        double basePrice = 10000;
        double areaPrice = property.getAreaSqft() != null ? property.getAreaSqft() * 10 : 0;
        double bedroomPrice = property.getBedrooms() != null ? property.getBedrooms() * 2000 : 0;
        double bathroomPrice = property.getBathrooms() != null ? property.getBathrooms() * 1500 : 0;
        double furnishedPrice = property.getIsFurnished() != null && property.getIsFurnished() ? 2000 : 0;
        double acPrice = property.getHasAc() != null && property.getHasAc() ? 1500 : 0;
        double ageDiscount = property.getPropertyAge() != null ? property.getPropertyAge() * (-100) : 0;

        double predicted = basePrice + areaPrice + bedroomPrice + bathroomPrice
                + furnishedPrice + acPrice + ageDiscount;
        return BigDecimal.valueOf(Math.max(predicted, 5000));
    }

    public PredictionHistory savePrediction(Long propertyId, BigDecimal predictedPrice) {
        Property property = propertyService.getPropertyById(propertyId);

        PredictionHistory history = new PredictionHistory();
        history.setProperty(property);
        history.setAreaSqft(property.getAreaSqft());
        history.setBedrooms(property.getBedrooms());
        history.setBathrooms(property.getBathrooms());
        history.setPropertyAge(property.getPropertyAge());
        history.setLocation(property.getLocation());
        history.setPredictedPrice(predictedPrice);
        history.setActualPrice(property.getRentPrice());

        return predictionRepository.save(history);
    }

    public List<PredictionHistory> getPredictionsByProperty(Long propertyId) {
        return predictionRepository.findByPropertyId(propertyId);
    }

    public PredictionHistory getLatestPrediction(Long propertyId) {
        return predictionRepository.findFirstByPropertyIdOrderByCreatedAtDesc(propertyId);
    }
}