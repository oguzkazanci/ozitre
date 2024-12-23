package com.trend.ozitre.repository;

import com.trend.ozitre.entity.LessonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonsRepository extends JpaRepository<LessonEntity,Long> {

    List<LessonEntity> findAllByLessonTypeOrderByLessonIdAsc(Long lessonType);

    List<LessonEntity> findAllByBookTypeOrderByLessonIdAsc(Long bookType);
}
