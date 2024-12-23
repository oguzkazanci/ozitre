package com.trend.ozitre.service;

import com.trend.ozitre.dto.TargetsDto;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface TargetsService {

    List<TargetsDto> getTargetsByStudentIdAndDateAndType(Long type, Long studentId, String strDate) throws ParseException;

    Long getTotalWorkTime(Long studentId, String strDate) throws ParseException;

    Long getTotalSolvedQuestion(Long studentId, String strDate) throws ParseException;

    TargetsDto saveTarget(TargetsDto targetsDto, String username);
}
