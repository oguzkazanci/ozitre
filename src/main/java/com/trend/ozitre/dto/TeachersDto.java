package com.trend.ozitre.dto;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class TeachersDto {

    private Long teacherId;
    private String username;
    private Integer teacherState;
    private Integer workType;
    private String teacherMail;
    private String teacherPhoneNumber;
    private String teacherName;
    private String teacherSurname;
    private String teacherAddress;
    private Set<LessonDto> lessons = new HashSet<>();
    private Long teacherLesPrice;
    private Long teacherBaseFee;
    private String explanation;
    private Long companyId;
}
