package com.rental.property_system.controller;

import com.rental.property_system.service.BookingService;
import com.rental.property_system.service.PropertyService;
import com.rental.property_system.repository.BookingRepository; // Fixed: Import added
import lombok.RequiredArgsConstructor;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.rental.property_system.entity.Property;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class FrontendController {

    private final PropertyService propertyService;
    private final BookingService bookingService;
    private final com.rental.property_system.service.UserService userService; 
    private final BookingRepository bookingRepository; // Fixed: Dependency injected
    private final com.rental.property_system.service.ReviewService reviewService;
    private final com.rental.property_system.repository.ReviewRepository reviewRepository;

    // This maps localhost:8080/ directly to homepage
    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("properties", propertyService.getAllProperties());
        return "index"; 
    }

    // 1. This shows the blank HTML form
    @GetMapping("/property/new")
    public String showAddPropertyForm(Model model) {
        model.addAttribute("property", new Property());
        return "add-property";
    }

    // 2. This catches the data when the user clicks "Submit"
    @PostMapping("/property/new")
    public String saveProperty(@ModelAttribute("property") Property property) {
        propertyService.addProperty(property);
        return "redirect:/"; 
    }

    // 1. Show the specific property details
    @GetMapping("/property/{propertyId}")
    public String viewPropertyDetails(@PathVariable("propertyId") Long propertyId, Model model) {
        Property property = propertyService.getPropertyById(propertyId);
        model.addAttribute("property", property);
        model.addAttribute("booking", new com.rental.property_system.entity.Booking());
        // Fetch all reviews for this specific property
        model.addAttribute("reviews", reviewRepository.findByProperty(property));
        return "property-details";
    }

    // 2. Handle the Booking Request
    @PostMapping("/property/{propertyId}/book")
    public String bookProperty(@PathVariable("propertyId") Long propertyId, 
                               @ModelAttribute("booking") com.rental.property_system.entity.Booking booking, 
                               Principal principal, // <-- Added Principal to know who is logged in!
                               org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            Property property = propertyService.getPropertyById(propertyId);
            
            // FETCH THE LOGGED-IN TENANT DYNAMICALLY
            String email = principal.getName();
            com.rental.property_system.entity.User tenant = userService.getUserByEmail(email); 
            
            booking.setProperty(property);
            booking.setTenant(tenant);
            
            bookingService.requestBooking(booking);
            
            redirectAttributes.addFlashAttribute("successMessage", "Booking request sent successfully!");
            // Redirect them to their own bookings page instead of home!
            return "redirect:/my-bookings"; 
            
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/property/" + propertyId; 
        }
    }

    // 1. Show the Owner Dashboard
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("bookings", bookingService.getAllBookings());
        return "dashboard";
    }

    // 2. Handle the Approve Button click
    @PostMapping("/dashboard/approve/{id}")
    public String approveBookingFromDashboard(@PathVariable("id") Long id) {
        bookingService.approveBooking(id);
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new com.rental.property_system.entity.User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") com.rental.property_system.entity.User user) {
        userService.registerUser(user);
        return "redirect:/login?registered";
    }

    @GetMapping("/my-bookings")
    public String showMyBookings(Model model, Principal principal) {
        String email = principal.getName();
        com.rental.property_system.entity.User tenant = userService.getUserByEmail(email);
        
        model.addAttribute("bookings", bookingRepository.findByTenant(tenant));
        return "my-bookings";
    }

    @PostMapping("/booking/{id}/cancel")
    public String cancelBooking(@PathVariable("id") Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            bookingService.cancelBooking(id);
            redirectAttributes.addFlashAttribute("successMessage", "Booking cancelled successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/my-bookings";
    }

    // Show the Review Form
    @GetMapping("/booking/{bookingId}/review")
    public String showReviewForm(@PathVariable("bookingId") Long bookingId, Model model) {
        com.rental.property_system.entity.Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid booking ID"));
        
        model.addAttribute("booking", booking);
        model.addAttribute("review", new com.rental.property_system.entity.Review());
        return "leave-review";
    }

    // Submit the Review
    @PostMapping("/booking/{bookingId}/review")
    public String submitReview(@PathVariable("bookingId") Long bookingId, 
                               @ModelAttribute("review") com.rental.property_system.entity.Review review, 
                               org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        
        com.rental.property_system.entity.Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        
        // Automatically link the review to the correct property and tenant based on the booking
        review.setProperty(booking.getProperty());
        review.setTenant(booking.getTenant());
        
        reviewService.saveReview(review);
        
        redirectAttributes.addFlashAttribute("successMessage", "Thank you for your review!");
        return "redirect:/my-bookings";
    }

    @PostMapping("/property/{id}/delete")
    public String deleteProperty(@PathVariable("id") Long id) {
        propertyService.deleteProperty(id);
        return "redirect:/";
    }
}