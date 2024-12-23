package com.trend.ozitre.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventWithPaymentDto {
    private EventsDto event;
    private PaymentDto payment;
}
