package com.trend.ozitre.repository;

import com.trend.ozitre.entity.SubjectProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectProgressRepository extends JpaRepository<SubjectProgressEntity, Long> {

    List<SubjectProgressEntity> getByStudentIdAndFoyId(Long studentId, Integer foyId);

    Optional<SubjectProgressEntity> getByStudentIdAndFoyIdAndFoyKod(Long studentId, Integer foyId, String foyKod);

}
