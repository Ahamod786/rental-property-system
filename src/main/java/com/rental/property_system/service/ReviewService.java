package com.rental.property_system.service;

import com.rental.property_system.entity.Review;
import com.rental.property_system.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public void saveReview(Review review) {
        reviewRepository.save(review);
    }
}