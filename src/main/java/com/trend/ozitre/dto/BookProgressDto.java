package com.trend.ozitre.dto;

import lombok.Data;

@Data
public class BookProgressDto {

    private Long bookProgressId;
    private Long givenBookId;
    private Long studentId;
    private Long bookId;
    private Long lessonId;
    private Long subjectId;
    private Long progress;
}
