package com.trend.ozitre.repository;

import com.trend.ozitre.entity.LibraryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibraryRepository extends JpaRepository<LibraryEntity, Long> {

    List<LibraryEntity> findAllByPublisherId(Long publisherId);
}
