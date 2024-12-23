package com.trend.ozitre.controller;

import com.trend.ozitre.dto.NotificationDto;
import com.trend.ozitre.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @CrossOrigin
    @GetMapping("/myNotifications")
    public ResponseEntity<List<NotificationDto>> getMyNotifications(Authentication authentication) {
        return ResponseEntity.ok(notificationService.getUserUnreadNotifications(((UserDetails) authentication.getPrincipal()).getUsername()));
    }

    @CrossOrigin
    @GetMapping("/size")
    public ResponseEntity<Long> getUnreadNotificationsSize(Authentication authentication) {
        return ResponseEntity.ok(notificationService.getUserUnreadNotificationsSize(((UserDetails) authentication.getPrincipal()).getUsername()));
    }
}
