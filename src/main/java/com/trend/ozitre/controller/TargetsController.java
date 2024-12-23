package com.trend.ozitre.controller;

import com.trend.ozitre.dto.TargetsDto;
import com.trend.ozitre.enums.Role;
import com.trend.ozitre.service.TargetsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/targets")
@Slf4j
public class TargetsController {

    private final TargetsService targetsService;

    @CrossOrigin
    @GetMapping(value = "/{type}/{studentId}/{date}")
    public ResponseEntity<List<TargetsDto>> getTargetsByStudentIdAndDateAndType(@PathVariable("type") Long type,
                                                                                @PathVariable("studentId") Long studentId,
                                                                                @PathVariable("date") String date,
                                                                                Authentication authentication) throws ParseException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN") ||
                authority.getAuthority().equals(Role.TEACHER.toString()))) {
            return ResponseEntity.ok(targetsService.getTargetsByStudentIdAndDateAndType(type, studentId, date));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @GetMapping("/getTotalWorkTime/{studentId}/{date}")
    public ResponseEntity<Long> getTotalWorkTime(@PathVariable("studentId") Long studentId,
                                                 @PathVariable("date") String date,
                                                 Authentication authentication) throws ParseException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN") ||
                authority.getAuthority().equals(Role.TEACHER.toString()))) {
            return ResponseEntity.ok(targetsService.getTotalWorkTime(studentId, date));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @GetMapping("/getTotalSolvedQuestion/{studentId}/{date}")
    public ResponseEntity<Long> getTotalSolvedQuestion(@PathVariable("studentId") Long studentId,
                                                       @PathVariable("date") String date,
                                                       Authentication authentication) throws ParseException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN") ||
                authority.getAuthority().equals(Role.TEACHER.toString()))) {
            return ResponseEntity.ok(targetsService.getTotalSolvedQuestion(studentId, date));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @PostMapping("/save")
    public ResponseEntity<TargetsDto> saveTargets(@RequestBody TargetsDto targetsDto, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN") ||
                authority.getAuthority().equals(Role.TEACHER.toString()))) {
            return ResponseEntity.ok(targetsService.saveTarget(targetsDto, userDetails.getUsername()));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }
}
