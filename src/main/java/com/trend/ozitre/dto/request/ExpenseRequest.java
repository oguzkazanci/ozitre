package com.trend.ozitre.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpenseRequest {
    private Long paymentStatus;
    private Long paymentType;
    private Integer month;
    private Long companyId;
}
