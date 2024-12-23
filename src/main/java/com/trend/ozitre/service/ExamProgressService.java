package com.trend.ozitre.service;

import com.trend.ozitre.dto.ExamProgressDto;

import java.util.List;

public interface ExamProgressService {

    List<ExamProgressDto> getExamProgressByStudentId(Long studentId);

    ExamProgressDto saveExamProgress(ExamProgressDto examProgressDto, String username);

    Boolean deleteExamProgress(Long examId);

}
