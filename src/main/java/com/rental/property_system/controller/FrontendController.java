package com.rental.property_system.controller;

import com.rental.property_system.entity.Booking;
import com.rental.property_system.entity.Property;
import com.rental.property_system.entity.PropertyImage;
import com.rental.property_system.entity.Review;
import com.rental.property_system.entity.User;
import com.rental.property_system.service.BookingService;
import com.rental.property_system.service.PaymentService;
import com.rental.property_system.service.PredictionService;
import com.rental.property_system.service.PropertyImageService;
import com.rental.property_system.service.PropertyService;
import com.rental.property_system.service.ReviewService;
import com.rental.property_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class FrontendController {

    private final PropertyService propertyService;
    private final PropertyImageService propertyImageService;
    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final ReviewService reviewService;
    private final PredictionService predictionService;
    private final UserService userService;

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("properties", propertyService.getAvailableProperties());
        return "index";
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
    public String registerUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        try {
            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful. Please login.");
            return "redirect:/login?registered=true";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/property/new")
    public String showAddPropertyForm(Model model) {
        model.addAttribute("property", new Property());
        model.addAttribute("isEdit", false);
        return "add-property";
    }

    @PostMapping("/property/new")
    public String saveProperty(@ModelAttribute Property property,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        try {
            User owner = currentUser(principal);
            Property savedProperty = propertyService.addProperty(property, owner.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Property added successfully.");
            return "redirect:/property/" + savedProperty.getId();
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/property/new";
        }
    }

    @GetMapping("/property/{propertyId}/edit")
    public String showEditPropertyForm(@PathVariable Long propertyId, Model model, Principal principal) {
        Property property = propertyService.getPropertyById(propertyId);
        assertOwner(property, currentUser(principal));
        model.addAttribute("property", property);
        model.addAttribute("isEdit", true);
        return "add-property";
    }

    @PostMapping("/property/{propertyId}/edit")
    public String updateProperty(@PathVariable Long propertyId,
                                 @ModelAttribute Property property,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        try {
            assertOwner(propertyService.getPropertyById(propertyId), currentUser(principal));
            propertyService.updateProperty(propertyId, property);
            redirectAttributes.addFlashAttribute("successMessage", "Property updated successfully.");
            return "redirect:/property/" + propertyId;
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/property/" + propertyId + "/edit";
        }
    }

    @PostMapping("/property/{propertyId}/upload-image")
    public String uploadPropertyImage(@PathVariable Long propertyId,
                                      @RequestParam("image") MultipartFile file,
                                      @RequestParam(value = "isPrimary", defaultValue = "false") Boolean isPrimary,
                                      Principal principal,
                                      RedirectAttributes redirectAttributes) {
        try {
            assertOwner(propertyService.getPropertyById(propertyId), currentUser(principal));
            propertyImageService.uploadImage(propertyId, file, isPrimary);
            redirectAttributes.addFlashAttribute("successMessage", "Image uploaded successfully.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/property/" + propertyId;
    }

    @PostMapping("/property/image/{imageId}/delete")
    public String deleteImage(@PathVariable Long imageId,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {
        Long propertyId = null;
        try {
            PropertyImage image = propertyImageService.getImageById(imageId);
            propertyId = image.getProperty().getId();
            assertOwner(image.getProperty(), currentUser(principal));
            propertyImageService.deleteImage(imageId);
            redirectAttributes.addFlashAttribute("successMessage", "Image deleted successfully.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/property/" + (propertyId == null ? "" : propertyId);
    }

    @GetMapping("/property/{propertyId}")
    public String viewPropertyDetails(@PathVariable Long propertyId, Model model) {
        Property property = propertyService.getPropertyById(propertyId);
        model.addAttribute("property", property);
        model.addAttribute("booking", new Booking());
        model.addAttribute("reviews", reviewService.getReviewsByProperty(propertyId));
        model.addAttribute("averageRating", reviewService.getAverageRating(propertyId));
        model.addAttribute("images", propertyImageService.getImagesByProperty(propertyId));
        model.addAttribute("primaryImage", propertyImageService.getPrimaryImage(propertyId));
        model.addAttribute("predictedPrice", predictionService.predictPrice(property));
        return "property-details";
    }

    @PostMapping("/property/{propertyId}/book")
    public String bookProperty(@PathVariable Long propertyId,
                               @RequestParam String startDate,
                               @RequestParam String endDate,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        try {
            User tenant = currentUser(principal);
            bookingService.requestBooking(propertyId, tenant.getId(), LocalDate.parse(startDate), LocalDate.parse(endDate));
            redirectAttributes.addFlashAttribute("successMessage", "Booking request sent successfully.");
            return "redirect:/my-bookings";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/property/" + propertyId;
        }
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, Principal principal) {
        User owner = currentUser(principal);
        model.addAttribute("properties", propertyService.getPropertiesByOwner(owner.getId()));
        model.addAttribute("bookings", bookingService.getBookingsForOwner(owner.getId()));
        return "dashboard";
    }

    @PostMapping("/dashboard/approve/{id}")
    public String approveBooking(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            assertOwner(bookingService.getBookingById(id).getProperty(), currentUser(principal));
            bookingService.approveBooking(id);
            redirectAttributes.addFlashAttribute("successMessage", "Booking approved.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/dashboard/reject/{id}")
    public String rejectBooking(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            assertOwner(bookingService.getBookingById(id).getProperty(), currentUser(principal));
            bookingService.rejectBooking(id);
            redirectAttributes.addFlashAttribute("successMessage", "Booking rejected.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/dashboard/complete/{id}")
    public String completeBooking(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            assertOwner(bookingService.getBookingById(id).getProperty(), currentUser(principal));
            bookingService.completeBooking(id);
            redirectAttributes.addFlashAttribute("successMessage", "Booking marked complete.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/my-bookings")
    public String showMyBookings(Model model, Principal principal) {
        User tenant = currentUser(principal);
        model.addAttribute("bookings", bookingService.getBookingsByTenant(tenant.getId()));
        return "my-bookings";
    }

    @PostMapping("/booking/{id}/cancel")
    public String cancelBooking(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            assertTenant(bookingService.getBookingById(id), currentUser(principal));
            bookingService.cancelBooking(id);
            redirectAttributes.addFlashAttribute("successMessage", "Booking cancelled.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/my-bookings";
    }

    @GetMapping("/checkout/{bookingId}")
    public String showCheckout(@PathVariable Long bookingId, Model model, Principal principal) {
        Booking booking = bookingService.getBookingById(bookingId);
        assertTenant(booking, currentUser(principal));
        model.addAttribute("booking", booking);
        return "checkout";
    }

    @PostMapping("/checkout/{bookingId}")
    public String processCheckout(@PathVariable Long bookingId,
                                  @RequestParam(defaultValue = "CARD") String paymentMethod,
                                  Principal principal,
                                  RedirectAttributes redirectAttributes) {
        try {
            User tenant = currentUser(principal);
            paymentService.processPayment(bookingId, tenant.getId(), paymentMethod);
            redirectAttributes.addFlashAttribute("successMessage", "Mock payment completed successfully.");
            return "redirect:/my-bookings";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/checkout/" + bookingId;
        }
    }

    @GetMapping("/booking/{bookingId}/review")
    public String showReviewForm(@PathVariable Long bookingId, Model model, Principal principal) {
        Booking booking = bookingService.getBookingById(bookingId);
        assertTenant(booking, currentUser(principal));
        model.addAttribute("booking", booking);
        model.addAttribute("review", new Review());
        return "leave-review";
    }

    @PostMapping("/booking/{bookingId}/review")
    public String submitReview(@PathVariable Long bookingId,
                               @RequestParam int rating,
                               @RequestParam String comment,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        try {
            Booking booking = bookingService.getBookingById(bookingId);
            User tenant = currentUser(principal);
            assertTenant(booking, tenant);
            reviewService.addReview(booking.getProperty().getId(), tenant.getId(), bookingId, rating, comment);
            redirectAttributes.addFlashAttribute("successMessage", "Thank you for your review.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/my-bookings";
    }

    @PostMapping("/property/{id}/delete")
    public String deleteProperty(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            Property property = propertyService.getPropertyById(id);
            assertOwner(property, currentUser(principal));
            propertyService.deleteProperty(id);
            redirectAttributes.addFlashAttribute("successMessage", "Property deleted successfully.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/dashboard";
    }

    private User currentUser(Principal principal) {
        if (principal == null) {
            throw new RuntimeException("Please login first");
        }
        return userService.getUserByEmail(principal.getName());
    }

    private void assertOwner(Property property, User user) {
        if (!property.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("You do not have permission to manage this property");
        }
    }

    private void assertTenant(Booking booking, User user) {
        if (!booking.getTenant().getId().equals(user.getId())) {
            throw new RuntimeException("You can only manage your own bookings");
        }
    }
}
