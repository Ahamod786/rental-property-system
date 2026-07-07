package com.rental.property_system.controller;

import com.rental.property_system.entity.Review;
import com.rental.property_system.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> addReview(
            @RequestParam Long propertyId,
            @RequestParam Long tenantId,
            @RequestParam Long bookingId,
            @RequestParam int rating,
            @RequestParam String comment) {

        Review review = reviewService.addReview(propertyId, tenantId, bookingId, rating, comment);
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<Review>> getReviewsByProperty(@PathVariable Long propertyId) {
        return ResponseEntity.ok(reviewService.getReviewsByProperty(propertyId));
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<Review>> getReviewsByTenant(@PathVariable Long tenantId) {
        return ResponseEntity.ok(reviewService.getReviewsByTenant(tenantId));
    }

    @GetMapping("/property/{propertyId}/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long propertyId) {
        return ResponseEntity.ok(reviewService.getAverageRating(propertyId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok("Review deleted successfully");
    }
}