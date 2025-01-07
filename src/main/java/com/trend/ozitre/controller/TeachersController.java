package com.trend.ozitre.controller;

import com.trend.ozitre.dto.TeachersDto;
import com.trend.ozitre.service.TeachersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teachers")
public class TeachersController {

    private final TeachersService teachersService;

    @GetMapping(value = "/{companyId}/{seasonId}")
    public ResponseEntity<List<TeachersDto>> getAllTeachers(@PathVariable("companyId") Long companyId,
                                                            @PathVariable("seasonId") Long seasonId,
                                                            Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(teachersService.getTeachers(companyId, seasonId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @PostMapping("/save/{companyId}")
    public ResponseEntity<TeachersDto> saveTeacher(@RequestBody TeachersDto teachersDto,
                                                   @PathVariable("companyId") Long companyId,
                                                   Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(teachersService.saveTeacher(teachersDto, userDetails.getUsername(), companyId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @GetMapping("/getByState/{state}/{companyId}")
    public ResponseEntity<List<TeachersDto>> getTeacherByState(@PathVariable("state") Integer state,
                                                               @PathVariable("companyId") Long companyId,
                                                               Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(teachersService.getTeacherByState(state, companyId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @GetMapping("/getByLessonId/{lessonId}/{companyId}")
    public ResponseEntity<List<TeachersDto>> getTeacherByLessonId(@PathVariable("lessonId") Long lessonId,
                                                                  @PathVariable("companyId") Long companyId,
                                                                  Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(teachersService.getTeacherByLessonId(lessonId, companyId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }
}
