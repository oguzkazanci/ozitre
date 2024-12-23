package com.trend.ozitre.controller;

import com.trend.ozitre.dto.UserDto;
import com.trend.ozitre.dto.response.BaseResponse;
import com.trend.ozitre.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @CrossOrigin
    @GetMapping("/getUserDetails")
    public ResponseEntity<UserDto> getUserDetails(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(userService.getUserDetails(userDetails.getUsername()));
    }

    @CrossOrigin
    @PostMapping("/changePassword/{oldPassword}/{newPassword}")
    public ResponseEntity<BaseResponse> changePassword(@PathVariable("oldPassword") String oldPassword,
                                                       @PathVariable("newPassword") String newPassword,
                                                       Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(userService.changePassword(oldPassword, newPassword, userDetails.getUsername()));
    }
}
