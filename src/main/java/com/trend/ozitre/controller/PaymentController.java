package com.trend.ozitre.controller;

import com.trend.ozitre.dto.PaymentDto;
import com.trend.ozitre.dto.TeachersDto;
import com.trend.ozitre.dto.request.ExpenseRequest;
import com.trend.ozitre.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @CrossOrigin
    @PostMapping("/savePayment/{companyId}")
    public ResponseEntity<PaymentDto> savePayment(@RequestBody PaymentDto paymentDto,
                                                 @PathVariable("companyId") Long companyId,
                                                 Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            PaymentDto paymentDto1 = paymentService.savePayment(paymentDto, userDetails.getUsername(), companyId);
            return ResponseEntity.ok(paymentDto1);
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @PostMapping("/updatePayment")
    public ResponseEntity<PaymentDto> updatePayment(@RequestBody PaymentDto paymentDto, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            PaymentDto paymentDto1 = paymentService.updatePayment(paymentDto, userDetails.getUsername());
            return ResponseEntity.ok(paymentDto1);
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @GetMapping("/getByEventId/{eventId}/{paymentType}")
    public ResponseEntity<PaymentDto> getPaymentByEventId(@PathVariable("eventId") Long eventId,
                                                          @PathVariable("paymentType") Integer paymentType,
                                                          Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(paymentService.getPaymentByEventId(eventId, paymentType));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @GetMapping("/getByEventIdAndStatus/{eventId}/{paymentType}")
    public ResponseEntity<PaymentDto> getPaymentByEventIdAndStatus(@PathVariable("eventId") Long eventId,
                                                                   @PathVariable("paymentType") Integer paymentType,
                                                                   @RequestParam Integer paymentStatus,
                                                                   Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(paymentService.getPaymentByEventIdAndStatus(eventId, paymentType, paymentStatus));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @GetMapping("/getPaymentPDF/{seasonId}/{studentId}")
    public byte[] getPDF(@PathVariable("studentId") Long studentId,
                         @PathVariable("seasonId") Long seasonId,
                         @RequestParam Integer month,
                         Authentication authentication) throws IOException, ParseException, URISyntaxException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if(userDetails.getAuthorities().stream().anyMatch((authority -> authority.getAuthority().equals("ADMIN")))) {
            return paymentService.getPaymentPdf(studentId, month, seasonId);
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @GetMapping("/getPaymentExcel/{seasonId}/{studentId}")
    public byte[] getExcel(@PathVariable("studentId") Long studentId,
                           @PathVariable("seasonId") Long seasonId,
                           @RequestParam Integer month,
                           Authentication authentication) throws IOException, ParseException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if(userDetails.getAuthorities().stream().anyMatch((authority -> authority.getAuthority().equals("ADMIN")))) {
            return paymentService.getPaymentExcel(studentId, month, seasonId);
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @GetMapping("/{companyId}/getTotalAmount/{type}")
    public ResponseEntity<Long> getTotalAmount(@PathVariable("type") Long type,
                                               @PathVariable("companyId") Long companyId,
                                               Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(paymentService.getTotalAmount(type, companyId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @PostMapping("/getExpenseTotalAmount")
    public ResponseEntity<List<PaymentDto>> getExpenseTotalAmount(@RequestBody ExpenseRequest expenseRequest,
                                                                  Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(paymentService.getExpenseTotalState(expenseRequest));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }
}
