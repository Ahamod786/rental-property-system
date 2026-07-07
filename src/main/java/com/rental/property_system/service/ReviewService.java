package com.rental.property_system.service;

import com.rental.property_system.entity.Booking;
import com.rental.property_system.entity.Property;
import com.rental.property_system.entity.Review;
import com.rental.property_system.entity.User;
import com.rental.property_system.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PropertyService propertyService;
    private final UserService userService;
    private final BookingService bookingService;

    @Transactional
    public Review addReview(Long propertyId, Long tenantId, Long bookingId, int rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }
        Booking booking = bookingService.getBookingById(bookingId);
        if (!booking.getTenant().getId().equals(tenantId) || !booking.getProperty().getId().equals(propertyId)) {
            throw new RuntimeException("Review does not match this booking");
        }
        if (!"PAID".equals(booking.getBookingStatus()) && !"COMPLETED".equals(booking.getBookingStatus())) {
            throw new RuntimeException("Reviews are allowed only after payment is completed");
        }
        if (!"SUCCESS".equals(booking.getPaymentStatus())) {
            throw new RuntimeException("You must complete payment before writing a review");
        }
        reviewRepository.findByBooking(booking).ifPresent(existing -> {
            throw new RuntimeException("You have already reviewed this booking");
        });

        Property property = propertyService.getPropertyById(propertyId);
        User tenant = userService.getUserById(tenantId);
        Review review = new Review();
        review.setProperty(property);
        review.setTenant(tenant);
        review.setBooking(booking);
        review.setRating(rating);
        review.setComment(comment);
        review.setReviewDate(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByProperty(Long propertyId) {
        return reviewRepository.findByPropertyOrderByIdDesc(propertyService.getPropertyById(propertyId));
    }

    public List<Review> getReviewsByTenant(Long tenantId) {
        return reviewRepository.findByTenantOrderByIdDesc(userService.getUserById(tenantId));
    }

    public double getAverageRating(Long propertyId) {
        List<Review> reviews = getReviewsByProperty(propertyId);
        return reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
    }
}
