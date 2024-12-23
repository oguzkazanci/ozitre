package com.trend.ozitre.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class TargetsDto {

    private Long targetId;
    private Long targetType;
    private Long targetStatus;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date targetDate;
    private Long studentId;
    private Long lessonId;
    private Long subjectId;
    private Long targetedNoQ;
    private Long solvedNoQ;
    private Long studyTimeinMin;
}
