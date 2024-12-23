package com.trend.ozitre.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
public class EnrollmentDto {

    private Long enrollmentId;
    private StudentsDto student;
    private LessonDto lesson;
    private TeachersDto teacher;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date firstDate;
    private Set<DayDto> days = new HashSet<>();
    private BigDecimal price;
    private Integer status;
    private Long companyId;
}