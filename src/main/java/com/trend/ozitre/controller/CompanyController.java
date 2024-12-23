package com.trend.ozitre.controller;

import com.trend.ozitre.dto.CompanyDto;
import com.trend.ozitre.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/company")
public class CompanyController {

    private final CompanyService companyService;

    @CrossOrigin
    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyDto> getCompanyDetails(@PathVariable("companyId") Long companyId,
                                                              Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(companyService.getCompanyDetails(companyId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @PostMapping("/save")
    public ResponseEntity<CompanyDto> saveGivenBook(@RequestBody CompanyDto company,
                                                    Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(companyService.saveCompany(company, userDetails.getUsername()));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }
}
