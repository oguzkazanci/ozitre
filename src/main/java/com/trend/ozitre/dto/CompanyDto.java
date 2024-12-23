package com.trend.ozitre.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class CompanyDto {

    private Long companyId;
    private Long companyType;
    private String companyTitle;
    private Long companySector;
    private String branchName;
    private String taxOffice;
    private Long taxNo;
    private Long mersisNo;
    private Long tradeRegisterNo;
    private String landPhone;
    private String faxNumber;
    private String gsmNumber;
    private String mailAddress;
    private Long provinceId;
    private Long districtId;
    private String companyAddress;
}
