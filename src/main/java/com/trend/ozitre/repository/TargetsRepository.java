package com.trend.ozitre.repository;

import com.trend.ozitre.entity.TargetsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface TargetsRepository extends JpaRepository<TargetsEntity,Long> {

    List<TargetsEntity> findByStudentIdAndTargetTypeAndTargetDateBetween(Long studentId, Long targetType, Date startDate, Date endDate);

    @Query("SELECT SUM(t.solvedNoQ) FROM TargetsEntity t WHERE t.targetDate BETWEEN :#{#startDate} AND :#{#endDate} AND t.studentId = :#{#studentId} AND t.targetType = 0 AND t.targetStatus != 2")
    Long getTotalSolvedQuestion(Long studentId, Date startDate, Date endDate);

    @Query("SELECT SUM(t.studyTimeinMin) FROM TargetsEntity t WHERE t.targetDate BETWEEN :#{#startDate} AND :#{#endDate} AND t.studentId = :#{#studentId} AND t.targetType = 1 AND t.targetStatus = 1")
    Long getTotalWorkTime(Long studentId, Date startDate, Date endDate);
}
