package com.trend.ozitre.dto.request;

import lombok.Data;

@Data
public class TokenRefreshRequest {

    private String refreshToken;

    private String username;
}
