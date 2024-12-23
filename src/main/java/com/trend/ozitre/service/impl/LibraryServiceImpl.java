package com.trend.ozitre.service.impl;

import com.trend.ozitre.dto.LibraryDto;
import com.trend.ozitre.entity.LibraryEntity;
import com.trend.ozitre.repository.LibraryRepository;
import com.trend.ozitre.service.LibraryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LibraryServiceImpl implements LibraryService {

    private final LibraryRepository libraryRepository;

    private final ModelMapper modelMapper;

    @Override
    public List<LibraryDto> getAllBooks() {
        List<LibraryEntity> libraryEntities = libraryRepository.findAll();
        return libraryEntities.stream().map(books -> modelMapper.map(books, LibraryDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<LibraryDto> getAllBookByPublisherId(Long publisherId) {
        List<LibraryEntity> entityList = libraryRepository.findAllByPublisherId(publisherId);
        return entityList.stream().map(book -> modelMapper.map(book, LibraryDto.class)).collect(Collectors.toList());
    }

    @Override
    public LibraryDto saveBook(LibraryDto libraryDto) {
        LibraryEntity libraryEntity = modelMapper.map(libraryDto, LibraryEntity.class);
        return modelMapper.map(libraryRepository.save(libraryEntity), LibraryDto.class);
    }

    @Override
    public Boolean removeBook(Long bookId) {
        Optional<LibraryEntity> book = libraryRepository.findById(bookId);

        if(book.isPresent()) {
            libraryRepository.deleteById(bookId);
            return true;
        }
        return false;
    }
}
