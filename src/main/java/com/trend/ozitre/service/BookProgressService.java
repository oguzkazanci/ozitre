package com.trend.ozitre.service;

import com.trend.ozitre.dto.BookProgressDto;

import java.util.List;

public interface BookProgressService {

    List<BookProgressDto> getBookProgressByStudentIdAndLessonId(Long studentId, Long lessonId);

    BookProgressDto saveBookProgress(BookProgressDto bookProgressDto, String username);
}
