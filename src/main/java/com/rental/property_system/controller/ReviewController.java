package com.rental.property_system.controller;

import com.rental.property_system.entity.Review;
import com.rental.property_system.entity.User;
import com.rental.property_system.service.ReviewService;
import com.rental.property_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    @GetMapping("/property/{propertyId}")
    public List<Review> byProperty(@PathVariable Long propertyId) {
        return reviewService.getReviewsByProperty(propertyId);
    }

    @PostMapping
    public ResponseEntity<Review> addReview(@RequestParam Long propertyId,
                                            @RequestParam Long bookingId,
                                            @RequestParam int rating,
                                            @RequestParam String comment,
                                            Principal principal) {
        User tenant = userService.getUserByEmail(principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.addReview(propertyId, tenant.getId(), bookingId, rating, comment));
    }
}
