package com.trend.ozitre.service.impl;

import com.trend.ozitre.dto.GivenBooksDto;
import com.trend.ozitre.entity.GivenBooksEntity;
import com.trend.ozitre.entity.LibraryEntity;
import com.trend.ozitre.repository.GivenBooksRepository;
import com.trend.ozitre.repository.LibraryRepository;
import com.trend.ozitre.service.GivenBooksService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GivenBooksServiceImpl implements GivenBooksService {

    private final GivenBooksRepository givenBooksRepository;

    private final LibraryRepository libraryRepository;

    private final ModelMapper modelMapper;

    @Override
    public List<GivenBooksDto> getAllGivenBooks() {
        List<GivenBooksEntity> givenBooksEntities = givenBooksRepository.findAll();
        return givenBooksEntities.stream().map(books -> modelMapper.map(books, GivenBooksDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<GivenBooksDto> getGivenBooksByStudentId(Long studentId) {
        List<GivenBooksEntity> givenBooksEntities = givenBooksRepository.findByStudentId(studentId);
        return givenBooksEntities.stream().map(givenBooks -> modelMapper.map(givenBooks, GivenBooksDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<GivenBooksDto> getGivenBook(Long studentId, Long lessonId, Long gradeId) {
        List<GivenBooksEntity> givenBooksEntities = givenBooksRepository.findByStudentIdAndLessonIdAndGradeId(studentId,
                lessonId, gradeId);
        return givenBooksEntities.stream().map(givenBooks -> modelMapper.map(givenBooks, GivenBooksDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<String[]> getLessonsByStudentId(Long studentId) {
        return givenBooksRepository.getGivenBooksLessonByStudentId(studentId);
    }

    @Override
    public GivenBooksDto saveGivenBook(GivenBooksDto givenBooksDto) {
        Optional<GivenBooksEntity> entityGB = Optional.empty();
        if (givenBooksDto.getGivenBookId() != null) entityGB = givenBooksRepository.findById(givenBooksDto.getGivenBookId());
        GivenBooksEntity givenBooks = modelMapper.map(givenBooksDto, GivenBooksEntity.class);

        Optional<LibraryEntity> book = libraryRepository.findById(givenBooksDto.getBookId());
        book.ifPresent(libraryEntity -> givenBooks.setGradeId(libraryEntity.getGradeId()));

        if (entityGB.isPresent()) {
            givenBooks.setCreatedBy(entityGB.get().getCreatedBy());
            givenBooks.setCreatedDate(entityGB.get().getCreatedDate());
            givenBooks.setUpdatedDate(new Date());
            givenBooks.setUpdatedBy("Admin");
        } else {
            givenBooks.setCreatedDate(new Date());
            givenBooks.setCreatedBy("Admin");
        }
        return modelMapper.map(givenBooksRepository.save(givenBooks), GivenBooksDto.class);
    }

    @Override
    public Boolean removeBook(Long givenBookId) {
        Optional<GivenBooksEntity> book = givenBooksRepository.findById(givenBookId);

        if(book.isPresent()) {
            givenBooksRepository.deleteById(givenBookId);
            return true;
        }
        return false;
    }
}
