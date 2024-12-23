package com.trend.ozitre.service;


import com.trend.ozitre.dto.NormalEnrollmentDto;

import java.util.List;

public interface NormalEnrollmentService {

    List<NormalEnrollmentDto> getNEnrollmentsByStudent(Long studentId);

    NormalEnrollmentDto getNEnrollment(Long nEnrollmentId);

    NormalEnrollmentDto saveNEnrollment(NormalEnrollmentDto normalEnrollmentDto, Long companyId, String username);
}
