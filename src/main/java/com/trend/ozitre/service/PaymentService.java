package com.trend.ozitre.service;

import com.trend.ozitre.dto.PaymentDto;
import com.trend.ozitre.dto.request.ExpenseRequest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;

public interface PaymentService {

    PaymentDto savePayment(PaymentDto paymentDto, String username, Long companyId);

    PaymentDto updatePayment(PaymentDto paymentDto, String username);

    PaymentDto getPaymentByEventId(Long eventId, Integer paymentType);

    PaymentDto getPaymentByEventIdAndStatus(Long eventId, Integer paymentType,Integer paymentStatus);

    byte[] getPaymentPdf(Long studentId, Integer month) throws IOException, ParseException, URISyntaxException;

    byte[] getPaymentExcel(Long studentId, Integer month) throws IOException, ParseException;

    Long getTotalAmount(Long type, Long companyId);

    List<PaymentDto> getExpenseTotalState(ExpenseRequest expenseRequest);
}
