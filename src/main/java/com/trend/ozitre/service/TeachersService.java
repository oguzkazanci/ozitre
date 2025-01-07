package com.trend.ozitre.service;

import com.trend.ozitre.dto.TeachersDto;
import com.trend.ozitre.entity.TeacherEntity;

import java.util.List;

public interface TeachersService {

    List<TeachersDto> getTeachers(Long companyId, Long seasonId);

    TeacherEntity getTeacher(Long id);

    List<TeachersDto> getTeacherByState(Integer state, Long companyId);

    List<TeachersDto> getTeacherByLessonId(Long lessonId, Long companyId);

    TeachersDto saveTeacher(TeachersDto teachersDto, String username, Long companyId);
}
