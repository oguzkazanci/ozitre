package com.trend.ozitre.dto;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class StudentsDto {

    private Long studentId;
    private String username;
    private String mail;
    private String phoneNumber;
    private Integer regState;
    private Integer regType;
    private String name;
    private String surname;
    private String address;
    private String school;
    private Long gradeId;
    private String parent;
    private String parentPhoNo;
    private String explanation;
    private Long packageId;
    private Integer totalPrice;
    private Integer startMonth;
    private Integer installment;
    private Long companyId;
}
