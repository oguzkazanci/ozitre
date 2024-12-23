package com.trend.ozitre.controller;

import com.trend.ozitre.dto.PaymentMethodDto;
import com.trend.ozitre.service.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/methods")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @CrossOrigin
    @GetMapping(value = "")
    public ResponseEntity<List<PaymentMethodDto>> getPaymentMethods(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(paymentMethodService.getMethods());
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }
}
