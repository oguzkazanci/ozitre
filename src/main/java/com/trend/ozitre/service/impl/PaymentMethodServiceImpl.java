package com.trend.ozitre.service.impl;

import com.trend.ozitre.dto.PaymentMethodDto;
import com.trend.ozitre.entity.PaymentMethodEntity;
import com.trend.ozitre.repository.PaymentMethodRepository;
import com.trend.ozitre.service.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    private final ModelMapper modelMapper;

    @Override
    public List<PaymentMethodDto> getMethods() {
        List<PaymentMethodEntity> paymentMethodEntities = paymentMethodRepository.findAll();
        return paymentMethodEntities.stream().map(student -> modelMapper.map(student, PaymentMethodDto.class)).collect(Collectors.toList());
    }
}
