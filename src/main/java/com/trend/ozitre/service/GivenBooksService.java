package com.trend.ozitre.service;

import com.trend.ozitre.dto.GivenBooksDto;

import java.util.List;

public interface GivenBooksService {

    List<GivenBooksDto> getAllGivenBooks();

    List<GivenBooksDto> getGivenBooksByStudentId(Long studentId);

    List<GivenBooksDto> getGivenBooksByStudentIdAndLessonId(Long studentId, Long lessonId);

    List<String[]> getLessonsByStudentId(Long studentId);

    GivenBooksDto saveGivenBook(GivenBooksDto givenBooksDto);

    Boolean removeBook(Long givenBookId);
}
