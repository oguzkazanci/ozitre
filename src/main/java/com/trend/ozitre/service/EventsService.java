package com.trend.ozitre.service;

import com.trend.ozitre.dto.EventWithPaymentDto;
import com.trend.ozitre.dto.EventsDto;
import com.trend.ozitre.entity.TeacherEntity;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface EventsService {

    List<EventsDto> getEvents(Long companyId);

    List<EventsDto> getLastEvents(Long companyId);

    List<EventsDto> getEventsByDateRange(String startDate, String endDate, Long companyId) throws ParseException;

    EventsDto addEvent(EventsDto eventsDto, String username, Long companyId);
    // TODO Düzenlenecek
    void addEventNew(EventsDto eventsDto, BigDecimal paymentAmount, String username, Long companyId);

    void addTeacherPayment(EventsDto eventsDto, TeacherEntity teacher, String username, Long companyId);

    EventsDto updateEvent(EventsDto eventsDto, String username);

    EventsDto addRoutineEvent(EventsDto eventsDto, Long repeatIntervalDays, String username, Long companyId);

    List<EventWithPaymentDto> getEventsByStudentId(Long studentId, Long seasonId, Integer month) throws ParseException;

    List<EventWithPaymentDto> getEventsByTeacherId(Long teacherId, Long seasonId, Integer month) throws ParseException;

    EventsDto getLastEventByStudentId(Long studentId);

    EventsDto getLastEventByTeacherId(Long teacherId);

    Long getSizeOfEvents(Long companyId);

    List<Date> monthToDate(Integer month, Long seasonId);

    List<Date> monthToDate(Integer month);
}
