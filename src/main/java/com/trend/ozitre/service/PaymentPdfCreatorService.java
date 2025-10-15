package com.trend.ozitre.service;

import com.trend.ozitre.dto.EventWithPaymentDto;
import com.trend.ozitre.dto.EventsDto;
import com.trend.ozitre.dto.PaymentDto;
import com.trend.ozitre.entity.EventsEntity;
import com.trend.ozitre.entity.StudentsEntity;
import com.trend.ozitre.model.AddressDetails;
import com.trend.ozitre.model.HeaderDetails;
import com.trend.ozitre.model.PackageTableHeader;
import com.trend.ozitre.model.ProductTableHeader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface PaymentPdfCreatorService {

    void createDocument() throws IOException, URISyntaxException;

    void createTnc(List<String> TncList, Boolean lastPage, String imagePath);

    void createEvent(List<EventWithPaymentDto> eventWithPaymentList, StudentsEntity student, List<PaymentDto> allPackagePayments);

    void createTableHeader(ProductTableHeader productTableHeader);

    void createTableHeader(PackageTableHeader packageTableHeader);

    void createAddress(Long studentId);

    void createHeader(HeaderDetails header);
}
