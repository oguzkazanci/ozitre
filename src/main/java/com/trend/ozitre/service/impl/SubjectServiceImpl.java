package com.trend.ozitre.service.impl;

import com.trend.ozitre.dto.SubjectDto;
import com.trend.ozitre.dto.request.SubjectRequest;
import com.trend.ozitre.entity.SubjectEntity;
import com.trend.ozitre.repository.SubjectRepository;
import com.trend.ozitre.service.SubjectService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;

    private final ModelMapper modelMapper;
    @Override
    public List<SubjectDto> getSubjects() {
        List<SubjectEntity> subjects = subjectRepository.findAll();
        return subjects.stream().map(subject -> modelMapper.map(subject, SubjectDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<SubjectDto> getSubjectsByLessonId(Long lessonId) {
        List<SubjectEntity> subjects = subjectRepository.findByLessonId(lessonId);
        return subjects.stream().map(subject -> modelMapper.map(subject, SubjectDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<SubjectDto> getSubjectsBySubjectRequest(SubjectRequest subjectRequest) {
        List<SubjectEntity> subjectEntities = subjectRepository.findByLessonIdAndGradeIdOrderByPosition(subjectRequest.getLessonId(),
                subjectRequest.getGradeId());
        return subjectEntities.stream().map(subject -> modelMapper.map(subject, SubjectDto.class)).collect(Collectors.toList());
    }

    @Override
    public SubjectDto addSubject(SubjectDto subjectDto) {
        SubjectEntity subject = modelMapper.map(subjectDto, SubjectEntity.class);

        Integer lastPosition = getLastPosition(subject.getGradeId(), subject.getLessonId());

        if (lastPosition == 0) subject.setPosition(lastPosition);
        else subject.setPosition(lastPosition + 1);

        return modelMapper.map(subjectRepository.save(subject), SubjectDto.class);
    }

    @Override
    public Boolean removeSubject(Long subjectId) {
        Optional<SubjectEntity> subject = subjectRepository.findById(subjectId);

        if(subject.isPresent()) {
            subjectRepository.deleteById(subjectId);
            return true;
        }
        return false;
    }

    @Override
    public void changePosition(Long subjectId, Integer oldPosition, Integer newPosition) {
        if (oldPosition.equals(newPosition)) {
            return;
        }

        SubjectEntity subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found with id: " + subjectId));

        if (newPosition > oldPosition) {
            subjectRepository.updatePositionsDown(subject.getGradeId(), subject.getLessonId(), oldPosition, newPosition);
        } else {
            subjectRepository.updatePositionsUp(subject.getGradeId(), subject.getLessonId(), oldPosition, newPosition);
        }

        subject.setPosition(newPosition);
        subjectRepository.save(subject);
    }

    Integer getLastPosition(long gradeId, long lessonId) {
        Integer position = subjectRepository.getMaxPositionByGradeIdAndLessonId(gradeId, lessonId);
        return (position != null) ? position : 0;
    }
}
