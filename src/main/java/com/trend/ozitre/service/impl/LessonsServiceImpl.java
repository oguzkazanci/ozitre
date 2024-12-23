package com.trend.ozitre.service.impl;

import com.trend.ozitre.dto.LessonDto;
import com.trend.ozitre.entity.LessonEntity;
import com.trend.ozitre.repository.LessonsRepository;
import com.trend.ozitre.service.LessonsService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonsServiceImpl implements LessonsService {

    private final ModelMapper modelMapper;

    private final LessonsRepository lessonsRepository;

    @Override
    public List<LessonDto> getLessons() {
        List<LessonEntity> lessonEntities = lessonsRepository.findAllByLessonTypeOrderByLessonIdAsc(0L);
        return lessonEntities.stream().map(lesson -> modelMapper.map(lesson, LessonDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<LessonDto> getLessonsForBook() {
        List<LessonEntity> lessonEntities = lessonsRepository.findAllByBookTypeOrderByLessonIdAsc(1L);
        return lessonEntities.stream().map(lesson -> modelMapper.map(lesson, LessonDto.class)).collect(Collectors.toList());
    }
}
