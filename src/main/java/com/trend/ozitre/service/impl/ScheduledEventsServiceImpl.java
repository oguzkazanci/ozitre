package com.trend.ozitre.service.impl;

import com.trend.ozitre.dto.EventsDto;
import com.trend.ozitre.entity.EnrollmentEntity;
import com.trend.ozitre.entity.ScheduledEventsEntity;
import com.trend.ozitre.repository.EnrollmentRepository;
import com.trend.ozitre.service.EventsService;
import com.trend.ozitre.service.ScheduledEventsService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ScheduledEventsServiceImpl implements ScheduledEventsService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    private final EventsService eventsService;

    @Autowired
    private ModelMapper modelMapper;

    private List<ScheduledEventsEntity> filterLessonsToProcess(List<ScheduledEventsEntity> recurringLessons, Date today) {
        // Başlangıç tarihi bugünden önce olan ve tekrar aralığına uygun olan dersler arasında filtreleme yap
        List<ScheduledEventsEntity> lessonsToProcess = new ArrayList<>();
        for (ScheduledEventsEntity lesson : recurringLessons) {
            Date startDate = lesson.getStartDate();
            Long repeatIntervalDays = lesson.getRepeatIntervalDays();

            // Başlangıç tarihinden bugüne kadar geçen gün sayısı
            long daysPassed = ChronoUnit.DAYS.between(startDate.toInstant(), today.toInstant());

            // Başlangıç tarihinden bugüne kadar geçen gün sayısı, tekrar aralığına bölünüp tam bölünüyorsa işlem yap
            if (daysPassed % repeatIntervalDays == 0) {
                lessonsToProcess.add(lesson);
            }
        }
        return lessonsToProcess;
    }
}
