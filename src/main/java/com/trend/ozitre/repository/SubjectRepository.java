package com.trend.ozitre.repository;

import com.trend.ozitre.entity.SubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SubjectRepository extends JpaRepository<SubjectEntity,Long> {

    List<SubjectEntity> findByLessonId(Long lessonId);

    List<SubjectEntity> findByLessonIdAndGradeIdOrderByPosition(Long lessonId, Long gradeId);

    @Query("SELECT MAX(s.position) FROM SubjectEntity s WHERE s.gradeId = :gradeId AND s.lessonId = :lessonId")
    Integer getMaxPositionByGradeIdAndLessonId(@Param("gradeId") long gradeId, @Param("lessonId") long lessonId);

    SubjectEntity getByGradeIdAndLessonIdAndPosition(Long gradeId, Long lessonId, Integer position);

    @Transactional
    @Modifying
    @Query("UPDATE SubjectEntity s SET s.position = s.position - 1 WHERE s.gradeId = :gradeId AND s.lessonId = :lessonId AND s.position > :oldPosition AND s.position <= :newPosition")
    void updatePositionsDown(@Param("gradeId") Long gradeId, @Param("lessonId") Long lessonId,
                             @Param("oldPosition") Integer oldPosition, @Param("newPosition") Integer newPosition);

    @Transactional
    @Modifying
    @Query("UPDATE SubjectEntity s SET s.position = s.position + 1 WHERE s.gradeId = :gradeId AND s.lessonId = :lessonId AND s.position >= :newPosition AND s.position < :oldPosition")
    void updatePositionsUp(@Param("gradeId") Long gradeId, @Param("lessonId") Long lessonId,
                           @Param("oldPosition") Integer oldPosition, @Param("newPosition") Integer newPosition);
}
