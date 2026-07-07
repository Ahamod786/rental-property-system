package com.rental.property_system.controller;

import com.rental.property_system.entity.Booking;
import com.rental.property_system.entity.Property;
import com.rental.property_system.entity.Review;
import com.rental.property_system.entity.User;
import com.rental.property_system.repository.BookingRepository;
import com.rental.property_system.repository.ReviewRepository;
import com.rental.property_system.service.BookingService;
import com.rental.property_system.service.PropertyService;
import com.rental.property_system.service.ReviewService;
import com.rental.property_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class FrontendController {

    private final PropertyService propertyService;
    private final BookingService bookingService;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("properties", propertyService.getAllProperties());
        return "index";
    }

    @GetMapping("/property/new")
    public String showAddPropertyForm(Model model) {
        model.addAttribute("property", new Property());
        return "add-property";
    }

    @PostMapping("/property/new")
    public String saveProperty(@ModelAttribute("property") Property property, Principal principal) {
        User owner = userService.getUserByEmail(principal.getName());
        propertyService.addProperty(property, owner.getId());
        return "redirect:/";
    }

    @GetMapping("/property/{propertyId}")
    public String viewPropertyDetails(@PathVariable("propertyId") Long propertyId, Model model) {
        Property property = propertyService.getPropertyById(propertyId);
        model.addAttribute("property", property);
        model.addAttribute("booking", new Booking());
        model.addAttribute("reviews", reviewRepository.findByProperty(property));
        model.addAttribute("averageRating", reviewService.getAverageRating(propertyId));
        return "property-details";
    }

    @PostMapping("/property/{propertyId}/book")
    public String bookProperty(
            @PathVariable("propertyId") Long propertyId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            User tenant = userService.getUserByEmail(principal.getName());
            bookingService.requestBooking(propertyId, tenant.getId(),
                    java.time.LocalDate.parse(startDate),
                    java.time.LocalDate.parse(endDate));
            redirectAttributes.addFlashAttribute("successMessage", "Booking request sent!");
            return "redirect:/my-bookings";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/property/" + propertyId;
        }
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("bookings", bookingService.getAllBookings());
        return "dashboard";
    }

    @PostMapping("/dashboard/approve/{id}")
    public String approveBookingFromDashboard(@PathVariable("id") Long id) {
        bookingService.approveBooking(id);
        return "redirect:/dashboard";
    }

    @PostMapping("/dashboard/reject/{id}")
    public String rejectBookingFromDashboard(@PathVariable("id") Long id) {
        bookingService.rejectBooking(id);
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user) {
        userService.registerUser(user);
        return "redirect:/login?registered";
    }

    @GetMapping("/my-bookings")
    public String showMyBookings(Model model, Principal principal) {
        User tenant = userService.getUserByEmail(principal.getName());
        model.addAttribute("bookings", bookingRepository.findByTenant(tenant));
        return "my-bookings";
    }

    @PostMapping("/booking/{id}/cancel")
    public String cancelBooking(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            bookingService.cancelBooking(id);
            redirectAttributes.addFlashAttribute("successMessage", "Booking cancelled.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/my-bookings";
    }

    @GetMapping("/booking/{bookingId}/review")
    public String showReviewForm(@PathVariable("bookingId") Long bookingId, Model model) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid booking ID"));
        model.addAttribute("booking", booking);
        model.addAttribute("review", new Review());
        return "leave-review";
    }

    @PostMapping("/booking/{bookingId}/review")
    public String submitReview(
            @PathVariable("bookingId") Long bookingId,
            @ModelAttribute("review") Review review,
            @RequestParam int rating,
            @RequestParam String comment,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            Booking booking = bookingRepository.findById(bookingId).orElseThrow();
            User tenant = userService.getUserByEmail(principal.getName());
            reviewService.addReview(booking.getProperty().getId(), tenant.getId(), bookingId, rating, comment);
            redirectAttributes.addFlashAttribute("successMessage", "Thank you for your review!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/my-bookings";
    }

    @PostMapping("/property/{id}/delete")
    public String deleteProperty(@PathVariable("id") Long id) {
        propertyService.deleteProperty(id);
        return "redirect:/";
    }
}