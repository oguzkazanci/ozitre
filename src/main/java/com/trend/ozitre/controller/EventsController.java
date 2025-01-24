package com.trend.ozitre.controller;

import com.trend.ozitre.dto.EventWithPaymentDto;
import com.trend.ozitre.dto.EventsDto;
import com.trend.ozitre.dto.StudentsDto;
import com.trend.ozitre.service.EventsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventsController {

    private final EventsService eventsService;

    @CrossOrigin
    @GetMapping(value = "/{companyId}")
    public ResponseEntity<List<EventsDto>> getAllEvents(@PathVariable("companyId") Long companyId,
                                                        Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(eventsService.getEvents(companyId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @GetMapping(value = "/{companyId}/date")
    public ResponseEntity<List<EventsDto>> getAllEventsByDate(@PathVariable("companyId") Long companyId,
                                                              @RequestParam String startDate,
                                                              @RequestParam String endDate,
                                                              Authentication authentication) throws Exception {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(eventsService.getEventsByDateRange(startDate, endDate, companyId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @PostMapping("/addEvent/{companyId}")
    public ResponseEntity<EventsDto> addEvent(@RequestBody EventsDto eventsDto,
                                              @PathVariable("companyId") Long companyId,
                                              Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(eventsService.addEvent(eventsDto, userDetails.getUsername(), companyId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @PostMapping("/update")
    public ResponseEntity<EventsDto> updateEvent(@RequestBody EventsDto eventsDto, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(eventsService.updateEvent(eventsDto, userDetails.getUsername()));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @PostMapping("/routineEvent/{repeatIntervalDays}/{companyId}")
    public ResponseEntity<EventsDto> saveRoutineEvent(@RequestBody EventsDto eventsDto,
                                                    @PathVariable("repeatIntervalDays") Long repeatIntervalDays,
                                                    @PathVariable("companyId") Long companyId,
                                                    Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(eventsService.addRoutineEvent(eventsDto, repeatIntervalDays, userDetails.getUsername(), companyId));
        } else throw new AccessDeniedException("Yetkiniz yok.");
    }

    @CrossOrigin
    @GetMapping("/getByStudentID/{seasonId}/{studentId}")
    public ResponseEntity<List<EventWithPaymentDto>> getEventsByStudentId(@PathVariable("studentId") Long studentId,
                                                                          @PathVariable("seasonId") Long seasonId,
                                                                          @RequestParam Integer month,
                                                                          Authentication authentication) throws ParseException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(eventsService.getEventsByStudentId(studentId, seasonId, month));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @GetMapping("/getLastEventByStudentID/{studentId}")
    public ResponseEntity<EventsDto> getLastEventByStudentId(@PathVariable("studentId") Long studentId, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(eventsService.getLastEventByStudentId(studentId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @GetMapping("/getByTeacherID/{seasonId}/{teacherId}")
    public ResponseEntity<List<EventWithPaymentDto>> getEventsByTeacherId(@PathVariable("teacherId") Long teacherId,
                                                                @PathVariable("seasonId") Long seasonId,
                                                                @RequestParam Integer month,
                                                                Authentication authentication) throws ParseException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(eventsService.getEventsByTeacherId(teacherId, seasonId, month));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @GetMapping("/getLastEventByTeacherID/{teacherId}")
    public ResponseEntity<EventsDto> getLastEventByTeacherId(@PathVariable("teacherId") Long teacherId,
                                                             Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(eventsService.getLastEventByTeacherId(teacherId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @GetMapping("/getSizeOfEvents/{companyId}")
    public ResponseEntity<Long> getSizeOfEvents(@PathVariable("companyId") Long companyId,
                                                Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(eventsService.getSizeOfEvents(companyId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }

    @CrossOrigin
    @GetMapping("/getLastEvents/{companyId}")
    public ResponseEntity<List<EventsDto>> getLastEvents(@PathVariable("companyId") Long companyId,
                                                         Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(eventsService.getLastEvents(companyId));
        } else {
            throw new AccessDeniedException("Yetkiniz yok.");
        }
    }
}
