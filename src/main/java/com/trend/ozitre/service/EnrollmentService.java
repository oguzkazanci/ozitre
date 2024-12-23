package com.trend.ozitre.service;

import com.trend.ozitre.dto.EnrollmentDto;
import com.trend.ozitre.dto.StudentsDto;

import java.util.List;

public interface EnrollmentService {

    List<EnrollmentDto> getEnrollmentsByStudent(Long studentId);

    EnrollmentDto getEnrollment(Long enrollmentId);

    EnrollmentDto saveEnrollment(EnrollmentDto enrollmentDto, Long companyId, String username);
}
