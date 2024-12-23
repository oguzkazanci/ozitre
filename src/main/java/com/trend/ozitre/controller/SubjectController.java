package com.trend.ozitre.controller;

import com.trend.ozitre.dto.SubjectDto;
import com.trend.ozitre.dto.request.SubjectRequest;
import com.trend.ozitre.enums.Role;
import com.trend.ozitre.service.SubjectService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subjects")
@Slf4j
public class SubjectController {

    private final SubjectService subjectService;

    @CrossOrigin
    @GetMapping(value = "")
    public ResponseEntity<List<SubjectDto>> getAllSubjects(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN") ||
                authority.getAuthority().equals(Role.TEACHER.toString()))) {
            return ResponseEntity.ok(subjectService.getSubjects());
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @GetMapping(value = "/getByLessonId/{lessonId}")
    public ResponseEntity<List<SubjectDto>> getAllSubjectsByLessonId(@PathVariable("lessonId") Long lessonId,
                                                                     Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN") ||
                authority.getAuthority().equals(Role.TEACHER.toString()))) {
            return ResponseEntity.ok(subjectService.getSubjectsByLessonId(lessonId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @PostMapping(value = "/getSubjectByLessonIdAndGradeId")
    public ResponseEntity<List<SubjectDto>> getSubjectByLessonIdAndGradeId(@RequestBody() SubjectRequest subjectRequest,
                                                                           Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN") ||
                authority.getAuthority().equals(Role.TEACHER.toString()))) {
            return ResponseEntity.ok(subjectService.getSubjectsBySubjectRequest(subjectRequest));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @PostMapping("/add")
    public ResponseEntity<SubjectDto> addSubject(@RequestBody SubjectDto subjectDto, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(subjectService.addSubject(subjectDto));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @DeleteMapping("/remove/{subjectId}")
    public ResponseEntity<Boolean> removeSubject(@PathVariable("subjectId") Long subjectId,
                                                 Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(subjectService.removeSubject(subjectId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @PutMapping("/{subjectId}/position")
    public ResponseEntity<String> changePosition(@PathVariable Long subjectId,
                                                 @RequestParam Integer oldPosition,
                                                 @RequestParam Integer newPosition,
                                                 Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            try {
                subjectService.changePosition(subjectId, oldPosition, newPosition);
                return ResponseEntity.ok("Position updated successfully");
            } catch (EntityNotFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Subject not found");
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
            }
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }
}
