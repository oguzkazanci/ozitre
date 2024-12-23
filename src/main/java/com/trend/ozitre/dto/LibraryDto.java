package com.trend.ozitre.dto;

import lombok.Data;

@Data
public class LibraryDto {

    private Long bookId;
    private String bookName;
    private Long lessonId;
    private Long publisherId;
    private Long gradeId;
}
