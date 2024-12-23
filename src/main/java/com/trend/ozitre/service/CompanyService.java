package com.trend.ozitre.service;

import com.trend.ozitre.dto.CompanyDto;

public interface CompanyService {

   CompanyDto getCompanyDetails(Long companyId);

    CompanyDto saveCompany(CompanyDto companyDto, String username);
}
