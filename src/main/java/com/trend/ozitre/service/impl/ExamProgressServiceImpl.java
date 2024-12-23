package com.trend.ozitre.service.impl;

import com.trend.ozitre.dto.ExamProgressDto;
import com.trend.ozitre.entity.ExamProgressEntity;
import com.trend.ozitre.repository.ExamProgressRepository;
import com.trend.ozitre.service.ExamProgressService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamProgressServiceImpl implements ExamProgressService {

    private final ExamProgressRepository examProgressRepository;

    private final ModelMapper modelMapper;

    @Override
    public List<ExamProgressDto> getExamProgressByStudentId(Long studentId) {
        List<ExamProgressEntity> examList = examProgressRepository.getAllByStudentId(studentId);
        return examList.stream().map(exam -> modelMapper.map(exam, ExamProgressDto.class)).collect(Collectors.toList());
    }

    @Override
    public ExamProgressDto saveExamProgress(ExamProgressDto examProgressDto, String username) {
        ExamProgressEntity examProgress = modelMapper.map(examProgressDto, ExamProgressEntity.class);

        Optional<ExamProgressEntity> optExam = examProgressRepository.getByExamId(examProgress.getExamId());

        if (optExam.isPresent()) {
            examProgress.setCreatedBy(optExam.get().getCreatedBy());
            examProgress.setCreatedDate(optExam.get().getCreatedDate());
            examProgress.setUpdatedBy(username);
            examProgress.setUpdatedDate(new Date());
        } else {
            examProgress.setCreatedBy(username);
            examProgress.setCreatedDate(new Date());
        }

        return modelMapper.map(examProgressRepository.save(examProgress), ExamProgressDto.class);
    }

    @Override
    public Boolean deleteExamProgress(Long examId) {
        Optional<ExamProgressEntity> optExam = examProgressRepository.findById(examId);

        if (optExam.isPresent()) {
            examProgressRepository.deleteById(optExam.get().getExamId());
            return true;
        }
        return false;
    }
}
