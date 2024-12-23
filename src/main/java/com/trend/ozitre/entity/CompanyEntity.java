package com.trend.ozitre.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Company")
@Data
public class CompanyEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "company_id")
    private Long companyId;
    @Column(name = "company_type", length = 2)
    private Long companyType;
    @Column(name = "company_title", length = 150)
    private String companyTitle;
    @Column(name = "company_sector", length = 20)
    private Long companySector;
    @Column(name = "branch_name", length = 20)
    private String branchName;
    @Column(name = "tax_office", length = 40)
    private String taxOffice;
    @Column(name = "tax_no", length = 15)
    private Long taxNo;
    @Column(name = "mersis_no", length = 16)
    private Long mersisNo;
    @Column(name = "trade_register_no", length = 20)
    private Long tradeRegisterNo;
    @Column(name = "land_phone", length = 16)
    private String landPhone;
    @Column(name = "fax_number", length = 16)
    private String faxNumber;
    @Column(name = "gsm_number", length = 16)
    private String gsmNumber;
    @Column(name = "mail_address", length = 100)
    private String mailAddress;
    @Column(name = "province_id", length = 6)
    private Long provinceId;
    @Column(name = "district_id", length = 200)
    private Long districtId;
    @Column(name = "company_address")
    private String companyAddress;
}
