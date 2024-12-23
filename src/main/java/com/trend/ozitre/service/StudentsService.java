package com.trend.ozitre.service;

import com.trend.ozitre.dto.StudentsDto;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;

public interface StudentsService {

    List<StudentsDto> getStudents(Long companyId) throws IOException, ParseException;

    List<StudentsDto> getStudentsByRegState(Integer regState, Long companyId);

    StudentsDto getStudent(Long id);

    StudentsDto saveStudent(StudentsDto studentsDto, String username, Long companyId);

    Boolean removeStudent(Long id);

    Long getSizeOfStudents(Long companyId);

    byte[] getRegistryPdf(Long studentId) throws IOException, URISyntaxException;

}
