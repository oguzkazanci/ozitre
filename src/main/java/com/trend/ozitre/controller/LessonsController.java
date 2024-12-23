package com.trend.ozitre.controller;

import com.trend.ozitre.dto.LessonDto;
import com.trend.ozitre.service.LessonsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lessons")
public class LessonsController {

    private final LessonsService lessonsService;

    @CrossOrigin
    @GetMapping(value = "")
    public ResponseEntity<List<LessonDto>> getAllLessons(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if(userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(lessonsService.getLessons());
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @GetMapping(value = "/type/{type}")
    public ResponseEntity<List<LessonDto>> getAllLessonsForBook(@PathVariable("type") Integer type,
                                                                Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if(userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(lessonsService.getLessonsForBook());
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }
}
