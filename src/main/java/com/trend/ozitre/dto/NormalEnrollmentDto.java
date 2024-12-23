package com.trend.ozitre.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class NormalEnrollmentDto {

    private Long nEnrollmentId;
    private StudentsDto student;
    private LessonDto lesson;
    private TeachersDto teacher;
    private BigDecimal price;
    private Integer status;
    private Long companyId;
}