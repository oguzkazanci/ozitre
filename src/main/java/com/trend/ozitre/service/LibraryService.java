package com.trend.ozitre.service;

import com.trend.ozitre.dto.LibraryDto;

import java.util.List;

public interface LibraryService {

    List<LibraryDto> getAllBooks();

    List<LibraryDto> getAllBookByPublisherId(Long publisherId);

    LibraryDto saveBook(LibraryDto libraryDto);

    Boolean removeBook(Long bookId);
}
