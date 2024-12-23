package com.trend.ozitre.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubjectProgressDto {

    private Long subjectProgressId;
    private Integer foyId;
    private String foyKod;
    private Long studentId;
    private Integer progress;
}
