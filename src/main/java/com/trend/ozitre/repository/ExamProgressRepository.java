package com.trend.ozitre.repository;

import com.trend.ozitre.entity.ExamProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamProgressRepository extends JpaRepository<ExamProgressEntity, Long> {

    List<ExamProgressEntity> getAllByStudentId(Long studentId);

    Optional<ExamProgressEntity> getByExamId(Long examId);
}
