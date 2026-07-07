package com.rental.property_system.service;

import com.rental.property_system.entity.Booking;
import com.rental.property_system.entity.Property;
import com.rental.property_system.entity.Review;
import com.rental.property_system.entity.User;
import com.rental.property_system.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingService bookingService;
    private final PropertyService propertyService;
    private final UserService userService;

    public Review addReview(Long propertyId, Long tenantId, Long bookingId,
                            int rating, String comment) {
        Property property = propertyService.getPropertyById(propertyId);
        User tenant = userService.getUserById(tenantId);
        Booking booking = bookingService.getBookingById(bookingId);

        if (!booking.getBookingStatus().equals("COMPLETED")) {
            throw new RuntimeException("Only completed bookings can be reviewed!");
        }
        if (!booking.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("You can only review your own bookings!");
        }
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5!");
        }

        Review review = new Review();
        review.setProperty(property);
        review.setTenant(tenant);
        review.setBooking(booking);
        review.setRating(rating);
        review.setComment(comment);

        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByProperty(Long propertyId) {
        Property property = propertyService.getPropertyById(propertyId);
        return reviewRepository.findByProperty(property);
    }

    public List<Review> getReviewsByTenant(Long tenantId) {
        User tenant = userService.getUserById(tenantId);
        return reviewRepository.findByTenant(tenant);
    }

    public Double getAverageRating(Long propertyId) {
        List<Review> reviews = getReviewsByProperty(propertyId);
        if (reviews.isEmpty()) return 0.0;
        return reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
    }

    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}