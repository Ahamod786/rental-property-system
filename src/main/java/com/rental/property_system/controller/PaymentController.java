package com.rental.property_system.controller;

import com.rental.property_system.entity.Booking;
import com.rental.property_system.entity.PaymentTransaction;
import com.rental.property_system.repository.BookingRepository;
import com.rental.property_system.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final BookingRepository bookingRepository;

    // Web - Show Checkout Page
    @GetMapping("/{bookingId}")
    public String showCheckoutPage(@PathVariable("bookingId") Long bookingId, Model model) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid booking ID"));
        model.addAttribute("booking", booking);
        return "checkout";
    }

    // Web - Process Payment
    @PostMapping("/process/{bookingId}")
    public String processPayment(
            @PathVariable("bookingId") Long bookingId,
            @RequestParam("paymentMethod") String paymentMethod,
            @RequestParam(value = "amount", required = false) BigDecimal amount,
            RedirectAttributes redirectAttributes) {

        try {
            paymentService.processPayment(bookingId, paymentMethod, amount);
            redirectAttributes.addFlashAttribute("successMessage", "Payment successful!");
            return "redirect:/my-bookings";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/checkout/" + bookingId;
        }
    }
}

// ============ REST API Payment Controller ============
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
class PaymentRestController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentTransaction> processPayment(
            @RequestParam Long bookingId,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) BigDecimal amount) {

        PaymentTransaction payment = paymentService.processPayment(bookingId, paymentMethod, amount);
        return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<PaymentTransaction>> getPaymentsByBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(paymentService.getPaymentsByBooking(bookingId));
    }

    @GetMapping("/reference/{ref}")
    public ResponseEntity<PaymentTransaction> getPaymentByRef(@PathVariable String ref) {
        return ResponseEntity.ok(paymentService.getPaymentByTransactionRef(ref));
    }
}