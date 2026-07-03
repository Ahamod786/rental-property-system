package com.rental.property_system.controller;

import com.rental.property_system.entity.Booking;
import com.rental.property_system.repository.BookingRepository;
import com.rental.property_system.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final BookingRepository bookingRepository;

    // 1. Show the mock payment page
    @GetMapping("/{bookingId}")
    public String showCheckoutPage(@PathVariable("bookingId") Long bookingId, Model model) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid booking ID"));
        
        model.addAttribute("booking", booking);
        return "checkout";
    }

    // 2. Process the payment form
    @PostMapping("/process/{bookingId}")
    public String processPayment(@PathVariable("bookingId") Long bookingId, 
                                 @RequestParam("paymentMethod") String paymentMethod,
                                 RedirectAttributes redirectAttributes) {
        
        paymentService.processPayment(bookingId, paymentMethod);
        
        redirectAttributes.addFlashAttribute("successMessage", "Payment Successful! Your property is officially secured.");
        return "redirect:/"; // Send them back to the home page with a success message
    }
}