package com.trend.ozitre.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class PaymentDto {

    private Long paymentId;
    private Long eventId;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date paymentDate;
    private Long paymentAmount;
    private Long remainingAmount;
    private Long amountReceived;
    private Long paymentQuantity;
    private Long paymentMethodId;
    private Integer paymentType;
    private Integer paymentStatus;
    private String explanation;
    private Integer repeatInterval;
    private Integer payBack;
}
