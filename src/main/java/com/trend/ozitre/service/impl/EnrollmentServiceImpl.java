package com.trend.ozitre.service.impl;

import com.trend.ozitre.dto.*;
import com.trend.ozitre.entity.DayEntity;
import com.trend.ozitre.entity.EnrollmentEntity;
import com.trend.ozitre.entity.TargetsEntity;
import com.trend.ozitre.repository.EnrollmentRepository;
import com.trend.ozitre.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public List<EnrollmentDto> getEnrollmentsByStudent(Long studentId) {
        List<EnrollmentDto> enrollments = new ArrayList<>();
        Set<DayDto> dayDtoSet = new HashSet<>();
        for (EnrollmentEntity enrollment : enrollmentRepository.getEnrollmentEntitiesByStudent_StudentIdAndStatus(studentId, 0)) {
            EnrollmentDto enrollmentDto = new EnrollmentDto();
            enrollmentDto.setEnrollmentId(enrollment.getEnrollmentId());
            enrollmentDto.setStudent(modelMapper.map(enrollment.getStudent(), StudentsDto.class));
            enrollmentDto.setLesson(modelMapper.map(enrollment.getLesson(), LessonDto.class));
            enrollmentDto.setTeacher(modelMapper.map(enrollment.getTeacher(), TeachersDto.class));
            for(DayEntity days: enrollment.getDays()) {
                dayDtoSet.add(modelMapper.map(days, DayDto.class));
            }
            enrollmentDto.setDays(dayDtoSet);
            enrollmentDto.setPrice(enrollment.getPrice());
            enrollmentDto.setFirstDate(enrollment.getFirstDate());
            enrollments.add(enrollmentDto);
        }
        return enrollments;
    }

    @Override
    public EnrollmentDto getEnrollment(Long enrollmentId) {
        EnrollmentEntity enrollment = enrollmentRepository.getReferenceById(enrollmentId);
        Set<DayDto> dayDtoSet = new HashSet<>();
        EnrollmentDto enrollmentDto = new EnrollmentDto();
        enrollmentDto.setEnrollmentId(enrollment.getEnrollmentId());
        enrollmentDto.setStudent(modelMapper.map(enrollment.getStudent(), StudentsDto.class));
        enrollmentDto.setLesson(modelMapper.map(enrollment.getLesson(), LessonDto.class));
        enrollmentDto.setTeacher(modelMapper.map(enrollment.getTeacher(), TeachersDto.class));
        for(DayEntity days: enrollment.getDays()) {
            dayDtoSet.add(modelMapper.map(days, DayDto.class));
        }
        enrollmentDto.setDays(dayDtoSet);
        enrollmentDto.setPrice(enrollment.getPrice());
        enrollmentDto.setFirstDate(enrollment.getFirstDate());
        return enrollmentDto;
    }

    @Override
    public EnrollmentDto saveEnrollment(EnrollmentDto enrollmentDto, Long companyId, String username) {
        Optional<EnrollmentEntity> enrollmentOpt = Optional.empty();
        if (enrollmentDto.getEnrollmentId() != null) enrollmentOpt = enrollmentRepository.findById(enrollmentDto.getEnrollmentId());

        EnrollmentEntity enrollment = modelMapper.map(enrollmentDto, EnrollmentEntity.class);
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
        return modelMapper.map(enrollmentRepository.save(enrollment), EnrollmentDto.class);
    }
}
