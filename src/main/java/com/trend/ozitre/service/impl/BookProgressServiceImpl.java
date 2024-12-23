package com.trend.ozitre.service.impl;

import com.trend.ozitre.dto.BookProgressDto;
import com.trend.ozitre.entity.BookProgressEntity;
import com.trend.ozitre.repository.BookProgressRepository;
import com.trend.ozitre.service.BookProgressService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookProgressServiceImpl implements BookProgressService {

    private final BookProgressRepository bookProgressRepository;

    private final ModelMapper modelMapper;

    @Override
    public List<BookProgressDto> getBookProgressByStudentIdAndLessonId(Long studentId, Long lessonId) {
        List<BookProgressEntity> bookProgressEntities = bookProgressRepository.findByStudentIdAndLessonId(studentId, lessonId);
        return bookProgressEntities.stream().map(bookProgress -> modelMapper.map(bookProgress, BookProgressDto.class)).collect(Collectors.toList());
    }

    @Override
    public BookProgressDto saveBookProgress(BookProgressDto bookProgressDto, String username) {
        BookProgressEntity bookProgress = modelMapper.map(bookProgressDto, BookProgressEntity.class);
        BookProgressEntity entityBP = bookProgressRepository.findByStudentIdAndGivenBookIdAndSubjectId(bookProgress.getStudentId(), bookProgress.getGivenBookId(), bookProgress.getSubjectId());

        if (entityBP != null) {
            bookProgress.setBookProgressId(entityBP.getBookProgressId());
            bookProgress.setCreatedBy(entityBP.getCreatedBy());
            bookProgress.setCreatedDate(entityBP.getCreatedDate());
            bookProgress.setUpdatedDate(new Date());
            bookProgress.setUpdatedBy(username);
        } else {
            bookProgress.setCreatedDate(new Date());
            bookProgress.setCreatedBy(username);
        }
        return modelMapper.map(bookProgressRepository.save(bookProgress), BookProgressDto.class);
    }
}
