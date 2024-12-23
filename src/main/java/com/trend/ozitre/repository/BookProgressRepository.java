package com.trend.ozitre.repository;

import com.trend.ozitre.entity.BookProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookProgressRepository extends JpaRepository<BookProgressEntity, Long> {

    List<BookProgressEntity> findByStudentIdAndLessonId(Long studentId, Long lessonId);

    BookProgressEntity findByStudentIdAndGivenBookIdAndSubjectId(Long studentId, Long givenBookId, Long subjectId);
}
