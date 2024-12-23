package com.trend.ozitre.repository;

import com.trend.ozitre.entity.StudentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface StudentsRepository extends JpaRepository<StudentsEntity,Long> {

    List<StudentsEntity> findByCompanyIdOrderByStudentIdAsc(Long companyId);
    List<StudentsEntity> findByCompanyIdAndRegStateEquals(Long companyId, Integer regState);
    Long countByCompanyIdAndRegState(Long companyId, Integer regState);
    List<StudentsEntity> findByPackageIdIsNotNull();
}
