package com.trend.ozitre.controller;

import com.trend.ozitre.dto.GivenBooksDto;
import com.trend.ozitre.enums.Role;
import com.trend.ozitre.service.GivenBooksService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/givenbooks")
public class GivenBooksController {

    private final GivenBooksService givenBooksService;

    @CrossOrigin
    @GetMapping()
    private ResponseEntity<List<GivenBooksDto>> getAll(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(givenBooksService.getAllGivenBooks());
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @GetMapping("/getByStudentId/{studentId}")
    private ResponseEntity<List<GivenBooksDto>> getGivenBooksByStudentId(@PathVariable("studentId") Long studentId,
                                                                         Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(givenBooksService.getGivenBooksByStudentId(studentId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @GetMapping("/getGivenBook/{studentId}/{lessonId}/{gradeId}")
    private ResponseEntity<List<GivenBooksDto>> getGivenBooksByStudentId(@PathVariable("studentId") Long studentId,
                                                                         @PathVariable("lessonId") Long lessonId,
                                                                         @PathVariable("gradeId") Long gradeId,
                                                                         Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN") ||
                authority.getAuthority().equals(Role.TEACHER.toString()))) {
            return ResponseEntity.ok(givenBooksService.getGivenBook(studentId, lessonId, gradeId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @GetMapping("/getLessonsByStudentId/{studentId}")
    private ResponseEntity<List<String[]>> getLessonsByStudentId(@PathVariable("studentId") Long studentId,
                                                               Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN") ||
                authority.getAuthority().equals(Role.TEACHER.toString()))) {
            return ResponseEntity.ok(givenBooksService.getLessonsByStudentId(studentId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @PostMapping("/save")
    public ResponseEntity<GivenBooksDto> saveGivenBook(@RequestBody GivenBooksDto givenBook,
                                                       Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(givenBooksService.saveGivenBook(givenBook));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @DeleteMapping("/remove/{givenBookId}")
    public ResponseEntity<Boolean> removeBookTracking(@PathVariable("givenBookId") Long givenBookId,
                                                      Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(givenBooksService.removeBook(givenBookId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }
}
