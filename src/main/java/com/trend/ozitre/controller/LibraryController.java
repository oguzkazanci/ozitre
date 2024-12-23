package com.trend.ozitre.controller;

import com.trend.ozitre.dto.LibraryDto;
import com.trend.ozitre.service.LibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/library")
public class LibraryController {

    private final LibraryService libraryService;

    @CrossOrigin
    @GetMapping()
    private ResponseEntity<List<LibraryDto>> getAllBooks(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(libraryService.getAllBooks());
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @GetMapping("/getByPublisherId/{publisherId}")
    public ResponseEntity<List<LibraryDto>> getTeacherByLessonId(@PathVariable("publisherId") Long publisherId,
                                                                 Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(libraryService.getAllBookByPublisherId(publisherId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @PostMapping("/save")
    private ResponseEntity<LibraryDto> saveBook(@RequestBody LibraryDto libraryDto, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(libraryService.saveBook(libraryDto));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @DeleteMapping("/remove/{bookId}")
    public ResponseEntity<Boolean> removeBook(@PathVariable("bookId") Long bookId,  Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(libraryService.removeBook(bookId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }
}
