package com.trend.ozitre.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BaseResponse {

    private String message;
    private String status;
}
