package com.trend.ozitre.controller;

import com.trend.ozitre.dto.EnrollmentDto;
import com.trend.ozitre.dto.NormalEnrollmentDto;
import com.trend.ozitre.dto.PackageDto;
import com.trend.ozitre.dto.StudentsDto;
import com.trend.ozitre.enums.Role;
import com.trend.ozitre.service.EnrollmentService;
import com.trend.ozitre.service.NormalEnrollmentService;
import com.trend.ozitre.service.PackageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.trend.ozitre.service.StudentsService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/students")
@Slf4j
public class StudentsController {

    @Autowired
    private StudentsService studentsService;

    @Autowired
    private PackageService packageService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private NormalEnrollmentService nEnrollmentService;

    @CrossOrigin
    @GetMapping(value = "/{companyId}/{seasonId}")
    public ResponseEntity<List<StudentsDto>> getAllStudents(@PathVariable("companyId") Long companyId,
                                                            @PathVariable("seasonId") Long seasonId,
                                                            Authentication authentication) throws IOException, ParseException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN") ||
                authority.getAuthority().equals(Role.TEACHER.toString()))) {
            return ResponseEntity.ok(studentsService.getStudents(companyId, seasonId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @GetMapping(value = "/state/{state}/{companyId}/{seasonId}")
    public ResponseEntity<List<StudentsDto>> getAllStudentsByRegState(@PathVariable("state") Integer regState,
                                                                      @PathVariable("companyId") Long companyId,
                                                                      @PathVariable("seasonId") Long seasonId,
                                                                      Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN") ||
                authority.getAuthority().equals(Role.TEACHER.toString()))) {
            return ResponseEntity.ok(studentsService.getStudentsByRegState(regState, companyId, seasonId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @PostMapping("/save/{companyId}")
    public ResponseEntity<StudentsDto> saveStudent(@RequestBody StudentsDto studentsDto,
                                                   @PathVariable("companyId") Long companyId,
                                                   Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(studentsService.saveStudent(studentsDto, userDetails.getUsername(), companyId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Boolean> removeStudent(@PathVariable("id") Long id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(studentsService.removeStudent(id));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @GetMapping("/getByID/{id}")
    public ResponseEntity<StudentsDto> getUser(@PathVariable("id") Long id,
                                               Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN") ||
                authority.getAuthority().equals(Role.TEACHER.toString()))) {
            return ResponseEntity.ok(studentsService.getStudent(id));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @GetMapping("/getSizeOfStudents/{companyId}")
    public ResponseEntity<Long> getSizeOfStudents(@PathVariable("companyId") Long companyId,
                                                  Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(studentsService.getSizeOfStudents(companyId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @GetMapping(value = "/getPackages")
    public ResponseEntity<List<PackageDto>> getAllStudents(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(packageService.getPackages());
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @PostMapping("/savePackage")
    public ResponseEntity<PackageDto> savePackage(@RequestBody PackageDto packageDto,
                                                  Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(packageService.savePackage(packageDto));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @GetMapping(value = "/getEnrollmentsByStudent/{studentId}")
    public ResponseEntity<List<EnrollmentDto>> getEnrollmentsByStudent(@PathVariable Long studentId,
                                                                       Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(enrollmentService.getEnrollmentsByStudent(studentId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @GetMapping(value = "/getEnrollment/{enrollmentId}")
    public ResponseEntity<EnrollmentDto> getEnrollment(@PathVariable Long enrollmentId,
                                                       Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(enrollmentService.getEnrollment(enrollmentId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @PostMapping("/saveEnrollment/{companyId}")
    public ResponseEntity<EnrollmentDto> saveEnrollment(@PathVariable Long companyId,
                                                        @RequestBody EnrollmentDto enrollmentDto,
                                                        Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(enrollmentService.saveEnrollment(enrollmentDto, companyId, userDetails.getUsername()));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @GetMapping(value = "/getNEnrollmentsByStudent/{studentId}")
    public ResponseEntity<List<NormalEnrollmentDto>> getNEnrollmentsByStudent(@PathVariable Long studentId,
                                                                              Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(nEnrollmentService.getNEnrollmentsByStudent(studentId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @GetMapping(value = "/getNEnrollment/{enrollmentId}")
    public ResponseEntity<NormalEnrollmentDto> getNEnrollment(@PathVariable Long enrollmentId,
                                                       Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(nEnrollmentService.getNEnrollment(enrollmentId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @PostMapping("/saveNEnrollment/{companyId}")
    public ResponseEntity<NormalEnrollmentDto> saveNEnrollment(@PathVariable Long companyId,
                                                               @RequestBody NormalEnrollmentDto nEnrollmentDto,
                                                               Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(nEnrollmentService.saveNEnrollment(nEnrollmentDto, companyId, userDetails.getUsername()));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @GetMapping("/getRegistryPDF/{studentId}")
    public byte[] getPDF(@PathVariable("studentId") Long studentId,
                         Authentication authentication) throws IOException, URISyntaxException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if(userDetails.getAuthorities().stream().anyMatch((authority -> authority.getAuthority().equals("ADMIN")))) {
            return studentsService.getRegistryPdf(studentId);
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }
}
