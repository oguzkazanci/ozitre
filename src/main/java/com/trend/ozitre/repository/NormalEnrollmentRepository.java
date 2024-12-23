package com.trend.ozitre.repository;

import com.trend.ozitre.entity.NormalEnrollmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NormalEnrollmentRepository extends JpaRepository<NormalEnrollmentEntity, Long> {

    List<NormalEnrollmentEntity> getByStudent_StudentIdAndStatus(Long studentId, Integer status);

}
