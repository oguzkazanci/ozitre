package com.trend.ozitre.service.impl;

import com.trend.ozitre.dto.SubjectProgressDto;
import com.trend.ozitre.dto.request.SubjectProgressRequest;
import com.trend.ozitre.entity.SubjectProgressEntity;
import com.trend.ozitre.repository.SubjectProgressRepository;
import com.trend.ozitre.service.SubjectProgressService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectProgressImpl implements SubjectProgressService {

    private final SubjectProgressRepository subjectProgressRepository;

    private final ModelMapper modelMapper;

    @Override
    public List<SubjectProgressDto> getProgressByStudentIdAndFoyId(SubjectProgressRequest progressRequest) {
        List<SubjectProgressEntity> progressEntities = subjectProgressRepository.getByStudentIdAndFoyId(progressRequest.getStudentId(), progressRequest.getFoyId());
        return progressEntities.stream().map(progress -> modelMapper.map(progress, SubjectProgressDto.class)).collect(Collectors.toList());
    }

    @Override
    public SubjectProgressDto saveSubjectProgress(SubjectProgressDto subjectProgressDto, String username) {
        SubjectProgressEntity progress = modelMapper.map(subjectProgressDto, SubjectProgressEntity.class);

        Optional<SubjectProgressEntity> optProgress = subjectProgressRepository.getByStudentIdAndFoyIdAndFoyKod(subjectProgressDto.getStudentId(),
                subjectProgressDto.getFoyId(), subjectProgressDto.getFoyKod());

        if (optProgress.isPresent()) {
            progress.setSubjectProgressId(optProgress.get().getSubjectProgressId());
            progress.setCreatedBy(optProgress.get().getCreatedBy());
            progress.setCreatedDate(optProgress.get().getCreatedDate());
            progress.setUpdatedDate(new Date());
            progress.setUpdatedBy(username);
        } else {
            progress.setCreatedBy(username);
            progress.setCreatedDate(new Date());
        }

        return modelMapper.map(subjectProgressRepository.save(progress), SubjectProgressDto.class);
    }
}
