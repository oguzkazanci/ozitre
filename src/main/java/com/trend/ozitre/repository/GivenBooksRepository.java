package com.trend.ozitre.repository;

import com.trend.ozitre.entity.GivenBooksEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GivenBooksRepository extends JpaRepository<GivenBooksEntity, Long> {

    List<GivenBooksEntity> findByStudentId(Long studentId);

    List<GivenBooksEntity> findByStudentIdAndLessonIdAndGradeId(Long studentId, Long lessonId, Long gradeId);

    @Query("SELECT DISTINCT l.lesson, g.lessonId, g.gradeId, gr.grade FROM GivenBooksEntity g " +
            "inner join LessonEntity l on l.lessonId = g.lessonId " +
            "inner join GradeEntity gr on g.gradeId = gr.gradeId " +
            "where g.studentId = :#{#studentId}")
    List<String[]> getGivenBooksLessonByStudentId(Long studentId);
}
