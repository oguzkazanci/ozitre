package com.trend.ozitre.dto.response;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class UserResponse {

    private String token;

    private Long id;

    private String refreshToken;

    private String username;

    private List<String> roles;

    private List<CompanyResponse> companies;
}
