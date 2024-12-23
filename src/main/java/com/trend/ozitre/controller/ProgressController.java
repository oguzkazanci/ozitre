package com.trend.ozitre.controller;

import com.trend.ozitre.dto.BookProgressDto;
import com.trend.ozitre.dto.ExamProgressDto;
import com.trend.ozitre.dto.SubjectProgressDto;
import com.trend.ozitre.dto.request.SubjectProgressRequest;
import com.trend.ozitre.enums.Role;
import com.trend.ozitre.service.BookProgressService;
import com.trend.ozitre.service.ExamProgressService;
import com.trend.ozitre.service.SubjectProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/progress")
public class ProgressController {

    private final BookProgressService bookProgressService;

    private final SubjectProgressService subjectProgressService;

    private final ExamProgressService examProgressService;

    @CrossOrigin
    @GetMapping("/getByStudentIdAndLessonId/{studentId}/{lessonId}")
    private ResponseEntity<List<BookProgressDto>> getBookProgressByStudentIdAndLessonId(@PathVariable("studentId") Long studentId,
                                                                                        @PathVariable("lessonId") Long lessonId,
                                                                                        Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN") ||
                authority.getAuthority().equals(Role.TEACHER.toString()))) {
            return ResponseEntity.ok(bookProgressService.getBookProgressByStudentIdAndLessonId(studentId, lessonId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @PostMapping("/saveBookProgress")
    public ResponseEntity<BookProgressDto> saveBookProgress(@RequestBody BookProgressDto bookProgress,
                                                            Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN") ||
                authority.getAuthority().equals(Role.TEACHER.toString()))) {
            return ResponseEntity.ok(bookProgressService.saveBookProgress(bookProgress, userDetails.getUsername()));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @PostMapping("/getSubjectProgressByStudentIdAndFoyId")
    private ResponseEntity<List<SubjectProgressDto>> getSubjectProgressByStudentIdAndFoyId(@RequestBody() SubjectProgressRequest progressRequest,
                                                                                           Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN") ||
                authority.getAuthority().equals(Role.TEACHER.toString()))) {
            return ResponseEntity.ok(subjectProgressService.getProgressByStudentIdAndFoyId(progressRequest));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @PostMapping("/saveSubjectProgress")
    public ResponseEntity<SubjectProgressDto> saveSubjectProgress(@RequestBody SubjectProgressDto subjectProgressDto,
                                                               Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN") ||
                authority.getAuthority().equals(Role.TEACHER.toString()))) {
            return ResponseEntity.ok(subjectProgressService.saveSubjectProgress(subjectProgressDto, userDetails.getUsername()));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @GetMapping("/getExamProgressByStudentId/{studentId}")
    private ResponseEntity<List<ExamProgressDto>> getExamProgressByStudentId(@PathVariable() Long studentId,
                                                                                Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN") ||
                authority.getAuthority().equals(Role.TEACHER.toString()))) {
            return ResponseEntity.ok(examProgressService.getExamProgressByStudentId(studentId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @PostMapping("/saveExamProgress")
    public ResponseEntity<ExamProgressDto> saveExamProgress(@RequestBody ExamProgressDto examProgressDto,
                                                            Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN") ||
                authority.getAuthority().equals(Role.TEACHER.toString()))) {
            return ResponseEntity.ok(examProgressService.saveExamProgress(examProgressDto, userDetails.getUsername()));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @DeleteMapping("/deleteExamProgress/{examId}")
    public ResponseEntity<Boolean> deleteExamProgress(@PathVariable("examId") Long examId,
                                                      Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN") ||
                authority.getAuthority().equals(Role.TEACHER.toString()))) {
            return ResponseEntity.ok(examProgressService.deleteExamProgress(examId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }
}
