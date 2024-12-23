package com.trend.ozitre.service;

import com.trend.ozitre.dto.PaymentMethodDto;

import java.util.List;

public interface PaymentMethodService {

    List<PaymentMethodDto> getMethods();
}
