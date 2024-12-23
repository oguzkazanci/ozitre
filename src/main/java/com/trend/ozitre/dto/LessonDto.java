package com.trend.ozitre.dto;

import lombok.Data;

@Data
public class LessonDto {

    private Long lessonId;
    private String lesson;
    private Long lessonType;
    private Long bookType;
}
