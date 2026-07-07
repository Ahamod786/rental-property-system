package com.rental.property_system.controller;

import com.rental.property_system.entity.PaymentTransaction;
import com.rental.property_system.entity.User;
import com.rental.property_system.service.PaymentService;
import com.rental.property_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService;

    @PostMapping("/pay")
    public ResponseEntity<PaymentTransaction> pay(@RequestParam Long bookingId,
                                                  @RequestParam(defaultValue = "CARD") String paymentMethod,
                                                  Principal principal) {
        User tenant = userService.getUserByEmail(principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.processPayment(bookingId, tenant.getId(), paymentMethod));
    }

    @PostMapping("/receipt")
    public PaymentTransaction receipt(@RequestParam Long bookingId, Principal principal) {
        User tenant = userService.getUserByEmail(principal.getName());
        return paymentService.getSuccessfulPaymentForBooking(bookingId, tenant.getId());
    }
}
