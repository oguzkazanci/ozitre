package com.trend.ozitre.controller;

import com.trend.ozitre.advice.TokenRefreshException;
import com.trend.ozitre.dto.request.TokenRefreshRequest;
import com.trend.ozitre.dto.response.MessageResponse;
import com.trend.ozitre.dto.UserDto;
import com.trend.ozitre.dto.request.UserRequest;
import com.trend.ozitre.dto.response.TokenRefreshResponse;
import com.trend.ozitre.entity.RefreshTokenEntity;
import com.trend.ozitre.entity.UserEntity;
import com.trend.ozitre.repository.UserRepository;
import com.trend.ozitre.service.AuthenticationService;
import com.trend.ozitre.service.RefreshTokenService;
import com.trend.ozitre.service.impl.UserDetailsImpl;
import com.trend.ozitre.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    private final RefreshTokenService refreshTokenService;

    private final JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody UserRequest requestDto) {
        return ResponseEntity.ok(authenticationService.authenticateUser(requestDto));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        MessageResponse messageResponse = authenticationService.registerUser(userDto);
        if (messageResponse.getMessage().contains("successfully")) {
            return ResponseEntity.ok(messageResponse);
        } else {
            return ResponseEntity.badRequest().body(messageResponse);
        }
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!Objects.equals(principle.toString(), "anonymousUser")) {
            Long userId = ((UserDetailsImpl) principle).getId();
            refreshTokenService.deleteByUserId(userId);
        }
        return ResponseEntity.ok(new MessageResponse("Log out successful!"));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Validated @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshTokenEntity::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getUsername());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }
}
