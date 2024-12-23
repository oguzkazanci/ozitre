package com.trend.ozitre.service;

import com.trend.ozitre.dto.SubjectProgressDto;
import com.trend.ozitre.dto.request.SubjectProgressRequest;

import java.util.List;

public interface SubjectProgressService {

    List<SubjectProgressDto> getProgressByStudentIdAndFoyId(SubjectProgressRequest progressRequest);

    SubjectProgressDto saveSubjectProgress(SubjectProgressDto subjectProgressDto, String username);
}
