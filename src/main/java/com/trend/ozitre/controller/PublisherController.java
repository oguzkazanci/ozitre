package com.trend.ozitre.controller;

import com.trend.ozitre.dto.PublisherDto;
import com.trend.ozitre.dto.SubjectDto;
import com.trend.ozitre.enums.Role;
import com.trend.ozitre.service.PublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/publisher")
public class PublisherController {

    private final PublisherService publisherService;

    @CrossOrigin
    @GetMapping(value = "")
    public ResponseEntity<List<PublisherDto>> getAllPublisher() {
        return ResponseEntity.ok((publisherService.getAllPublisher()));
    }

    @CrossOrigin
    @PostMapping("/save")
    public ResponseEntity<PublisherDto> savePublisher(@RequestBody PublisherDto publisherDto,
                                                      Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if(userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(publisherService.savePublisher(publisherDto));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @DeleteMapping("/remove/{publisherId}")
    public ResponseEntity<Boolean> removePublisher(@PathVariable("publisherId") Long publisherId,
                                                   Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if(userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(publisherService.removePublisher(publisherId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

}
