package com.trend.ozitre.service.impl;

import com.trend.ozitre.dto.LessonDto;
import com.trend.ozitre.dto.NormalEnrollmentDto;
import com.trend.ozitre.dto.StudentsDto;
import com.trend.ozitre.dto.TeachersDto;
import com.trend.ozitre.entity.NormalEnrollmentEntity;
import com.trend.ozitre.repository.NormalEnrollmentRepository;
import com.trend.ozitre.service.NormalEnrollmentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class NormalEnrollmentServiceImpl implements NormalEnrollmentService {

    @Autowired
    NormalEnrollmentRepository normalEnrollmentRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public List<NormalEnrollmentDto> getNEnrollmentsByStudent(Long studentId) {
        List<NormalEnrollmentDto> enrollments = new ArrayList<>();
        for (NormalEnrollmentEntity normalEnrollment : normalEnrollmentRepository.getByStudent_StudentIdAndStatus(studentId, 0)) {
            NormalEnrollmentDto enrollmentDto = new NormalEnrollmentDto();
            enrollmentDto.setNEnrollmentId(normalEnrollment.getNEnrollmentId());
            enrollmentDto.setStudent(modelMapper.map(normalEnrollment.getStudent(), StudentsDto.class));
            enrollmentDto.setLesson(modelMapper.map(normalEnrollment.getLesson(), LessonDto.class));
            enrollmentDto.setTeacher(modelMapper.map(normalEnrollment.getTeacher(), TeachersDto.class));
            enrollmentDto.setPrice(normalEnrollment.getPrice());
            enrollments.add(enrollmentDto);
        }
        return enrollments;
    }

    @Override
    public NormalEnrollmentDto getNEnrollment(Long nEnrollmentId) {
        NormalEnrollmentEntity enrollment = normalEnrollmentRepository.getReferenceById(nEnrollmentId);
        NormalEnrollmentDto enrollmentDto = new NormalEnrollmentDto();
        enrollmentDto.setNEnrollmentId(enrollment.getNEnrollmentId());
        enrollmentDto.setStudent(modelMapper.map(enrollment.getStudent(), StudentsDto.class));
        enrollmentDto.setLesson(modelMapper.map(enrollment.getLesson(), LessonDto.class));
        enrollmentDto.setTeacher(modelMapper.map(enrollment.getTeacher(), TeachersDto.class));
        enrollmentDto.setPrice(enrollment.getPrice());
        return enrollmentDto;
    }

    @Override
    public NormalEnrollmentDto saveNEnrollment(NormalEnrollmentDto normalEnrollmentDto, Long companyId, String username) {
        Optional<NormalEnrollmentEntity> enrollmentOpt = Optional.empty();
        if (normalEnrollmentDto.getNEnrollmentId() != null) enrollmentOpt = normalEnrollmentRepository.findById(normalEnrollmentDto.getNEnrollmentId());

        NormalEnrollmentEntity enrollment = modelMapper.map(normalEnrollmentDto, NormalEnrollmentEntity.class);
        enrollment.setCompanyId(companyId);
        if (enrollmentOpt.isPresent()) {
            enrollment.setCreatedBy(enrollmentOpt.get().getCreatedBy());
            enrollment.setCreatedDate(enrollmentOpt.get().getCreatedDate());
            enrollment.setUpdatedDate(new Date());
            enrollment.setUpdatedBy(username);
        } else {
            enrollment.setCreatedDate(new Date());
            enrollment.setCreatedBy(username);
        }
        return modelMapper.map(normalEnrollmentRepository.save(enrollment), NormalEnrollmentDto.class);
    }
}
