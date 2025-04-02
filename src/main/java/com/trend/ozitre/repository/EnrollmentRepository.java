package com.trend.ozitre.repository;

import com.trend.ozitre.entity.EnrollmentEntity;
import com.trend.ozitre.entity.StudentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<EnrollmentEntity, Long> {

    List<EnrollmentEntity> getEnrollmentEntitiesByStudent_StudentIdAndStatus(Long studentId, Integer status);

    List<EnrollmentEntity> getEnrollmentEntitiesByDays_NameAndFirstDateBeforeAndStatus(String dayName, Date firstDate, Integer status);

}
