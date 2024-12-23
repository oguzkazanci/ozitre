package com.trend.ozitre.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamProgressDto {

    private Long examId;
    private String examName;
    private String examDate;
    private Long studentId;
    private Integer turkishD;
    private Integer turkishY;
    private Integer turkishN;
    private Integer mathD;
    private Integer mathY;
    private Integer mathN;
    private Integer scienceD;
    private Integer scienceY;
    private Integer scienceN;
    private Integer socialD;
    private Integer socialY;
    private Integer socialN;
}
