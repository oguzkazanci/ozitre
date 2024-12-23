package com.trend.ozitre.service.impl;

import com.trend.ozitre.dto.GradeDto;
import com.trend.ozitre.entity.GradeEntity;
import com.trend.ozitre.repository.GradeRepository;
import com.trend.ozitre.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {

    private final GradeRepository gradeRepository;

    private final ModelMapper modelMapper;

    @Override
    public List<GradeDto> getGrades() {
        List<GradeEntity> gradeEntities = gradeRepository.findAll();
        return gradeEntities.stream().map(student -> modelMapper.map(student, GradeDto.class)).collect(Collectors.toList());
    }
}
