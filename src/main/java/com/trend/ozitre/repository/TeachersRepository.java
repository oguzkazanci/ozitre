package com.trend.ozitre.repository;

import com.trend.ozitre.entity.TeacherEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeachersRepository extends JpaRepository<TeacherEntity,Long> {

    List<TeacherEntity> findByCompanyIdAndSeasonIdOrderByTeacherIdAsc(Long companyId, Long seasonId);
    List<TeacherEntity> findByCompanyIdAndTeacherStateEquals(Long companyId, Integer teacherState);
    List<TeacherEntity> findByLessons_LessonIdAndTeacherStateAndCompanyId(Long teacherLessonId, Integer teacherState, Long companyId);

    List<TeacherEntity> findByTeacherBaseFeeIsNotNull();
}
