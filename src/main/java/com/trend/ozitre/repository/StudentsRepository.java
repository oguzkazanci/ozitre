package com.trend.ozitre.repository;

import com.trend.ozitre.entity.StudentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface StudentsRepository extends JpaRepository<StudentsEntity,Long> {

    List<StudentsEntity> findByCompanyIdAndSeasonIdOrderByStudentIdAsc(Long companyId, Long seasonId);
    List<StudentsEntity> findByCompanyIdAndSeasonIdAndRegStateEquals(Long companyId, Long seasonId,Integer regState);
    Long countByCompanyIdAndRegState(Long companyId, Integer regState);
    List<StudentsEntity> findByPackageIdIsNotNull();
}
