package com.trend.ozitre.service;

import com.trend.ozitre.dto.SubjectDto;
import com.trend.ozitre.dto.request.SubjectRequest;

import java.util.List;

public interface SubjectService {

    List<SubjectDto> getSubjects();

    List<SubjectDto> getSubjectsByLessonId(Long lessonId);

    List<SubjectDto> getSubjectsBySubjectRequest(SubjectRequest subjectRequest);

    SubjectDto addSubject(SubjectDto subjectDto);

    Boolean removeSubject(Long subjectId);

    void changePosition(Long subjectId, Integer oldPosition, Integer newPosition);
}
