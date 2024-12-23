package com.trend.ozitre.controller;

import com.trend.ozitre.dto.GradeDto;
import com.trend.ozitre.dto.StudentsDto;
import com.trend.ozitre.service.GradeService;
import com.trend.ozitre.service.StudentsService;
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
@RequestMapping("/api/grades")
public class GradeController {

    private final GradeService gradeService;

    @CrossOrigin
    @GetMapping(value = "")
    public ResponseEntity<List<GradeDto>> getAllGrades(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(gradeService.getGrades());
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }
}
