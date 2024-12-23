package com.trend.ozitre.service.impl;

import com.trend.ozitre.dto.TargetsDto;
import com.trend.ozitre.entity.TargetsEntity;
import com.trend.ozitre.repository.TargetsRepository;
import com.trend.ozitre.service.TargetsService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TargetsServiceImpl implements TargetsService {

    private final TargetsRepository targetsRepository;

    private final ModelMapper modelMapper;

    @Override
    public List<TargetsDto> getTargetsByStudentIdAndDateAndType(Long type, Long studentId, String strDate) throws ParseException {
        Date startDate = new SimpleDateFormat("dd-MM-yyyy").parse(strDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        Date endDate = calendar.getTime();
        List<TargetsEntity> targets = targetsRepository.findByStudentIdAndTargetTypeAndTargetDateBetween(studentId, type, startDate, endDate);
        List<TargetsDto> targetsDtos = targets.stream().map(target -> modelMapper.map(target, TargetsDto.class)).collect(Collectors.toList());
        return targetsDtos;
    }

    @Override
    public Long getTotalWorkTime(Long studentId, String strDate) throws ParseException {
        Date startDate = new SimpleDateFormat("dd-MM-yyyy").parse(strDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        Date endDate = calendar.getTime();
        return targetsRepository.getTotalWorkTime(studentId, startDate, endDate);
    }

    @Override
    public Long getTotalSolvedQuestion(Long studentId, String strDate) throws ParseException {
        Date startDate = new SimpleDateFormat("dd-MM-yyyy").parse(strDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        Date endDate = calendar.getTime();
        return targetsRepository.getTotalSolvedQuestion(studentId, startDate, endDate);
    }

    @Override
    public TargetsDto saveTarget(TargetsDto targetsDto, String username) {
        Optional<TargetsEntity> targetsEntity = null;
        if (targetsDto.getTargetId() != null) targetsEntity = targetsRepository.findById(targetsDto.getTargetId());
        TargetsEntity target = modelMapper.map(targetsDto, TargetsEntity.class);
        if (target.getSolvedNoQ() == null) target.setSolvedNoQ(0L);
        if (target.getTargetedNoQ() == null) target.setTargetedNoQ(0L);
        if (target.getStudyTimeinMin() == null) target.setStudyTimeinMin(0L);
        if (target.getTargetType() == 0) {
            if (target.getTargetedNoQ() > target.getSolvedNoQ()) { target.setTargetStatus(0L); }
            else if(target.getTargetedNoQ() <= target.getSolvedNoQ()) { target.setTargetStatus(1L); }
            else { target.setTargetStatus(2L); }
        }
        else if (target.getTargetType() == 1) {
            if (target.getTargetStatus() == null) target.setTargetStatus(1L);
        }

        // add or update
        if (targetsEntity != null) {
            target.setCreatedBy(targetsEntity.get().getCreatedBy());
            target.setCreatedDate(targetsEntity.get().getCreatedDate());
            target.setUpdatedDate(new Date());
            target.setUpdatedBy(username);
        } else {
            target.setCreatedDate(new Date());
            target.setCreatedBy(username);
        }
        target = targetsRepository.save(target);
        return modelMapper.map(target, TargetsDto.class);
    }
}
