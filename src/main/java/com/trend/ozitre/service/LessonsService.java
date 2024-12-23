package com.trend.ozitre.service;

import com.trend.ozitre.dto.LessonDto;

import java.util.List;

public interface LessonsService {

    List<LessonDto> getLessons();

    List<LessonDto> getLessonsForBook();
}
