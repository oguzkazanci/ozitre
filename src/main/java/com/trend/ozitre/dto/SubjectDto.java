package com.trend.ozitre.dto;

import lombok.Data;

@Data
public class SubjectDto {

    private Long subjectId;
    private Long lessonId;
    private String subject;
    private Long gradeId;
    private Integer position;

}
