package com.trend.ozitre.service;

import com.trend.ozitre.entity.StudentsEntity;
import com.trend.ozitre.model.HeaderDetails;
import com.trend.ozitre.model.PackageTableHeader;
import com.trend.ozitre.model.ProductTableHeader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface RegistryPdfCreatorService {

    void createDocument() throws IOException, URISyntaxException;

    void createTnc(List<String> TncList, Boolean lastPage, String imagePath);

    void createEvent(StudentsEntity studentsEntity);

    void createTableHeader(ProductTableHeader productTableHeader);

    void createTableHeader(PackageTableHeader packageTableHeader);

    void createAddress(StudentsEntity studentsEntity);

    void createHeader(HeaderDetails header);
}
