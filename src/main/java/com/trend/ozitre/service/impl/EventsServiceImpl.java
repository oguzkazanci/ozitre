package com.trend.ozitre.service.impl;

import com.trend.ozitre.dto.*;
import com.trend.ozitre.entity.*;
import com.trend.ozitre.repository.EventsRepository;
import com.trend.ozitre.repository.PaymentRepository;
import com.trend.ozitre.repository.SeasonsRepository;
import com.trend.ozitre.repository.StudentsRepository;
import com.trend.ozitre.service.EventsService;
import com.trend.ozitre.service.PaymentService;
import com.trend.ozitre.service.StudentsService;
import com.trend.ozitre.service.TeachersService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventsServiceImpl implements EventsService {

    private final EventsRepository eventsRepository;

    private final PaymentRepository paymentRepository;

    private final StudentsRepository studentsRepository;

    private final SeasonsRepository seasonsRepository;

    private final TeachersService teachersService;

    @Autowired
    @Lazy
    private PaymentService paymentService;

    private final ModelMapper modelMapper;

    @Override
    public List<EventsDto> getEvents(Long companyId) {
        List<EventsEntity> events = eventsRepository.findByEventStatusAndCompanyIdAndTitleIsNotLike(true, companyId, "%Düzenli%");
        return events.stream().map(event -> modelMapper.map(event, EventsDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<EventsDto> getLastEvents(Long companyId) {
        List<EventsEntity> events = eventsRepository.findTop7ByEventStatusAndCompanyId(true, companyId);
        return events.stream().map(event -> modelMapper.map(event, EventsDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<EventsDto> getEventsByDateRange(String startDate, String endDate, Long companyId) throws ParseException {
        Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(startDate);
        Date date2 = new SimpleDateFormat("dd-MM-yyyy").parse(endDate);
        List<EventsEntity> events = eventsRepository.findByDateBetweenAndCompanyId(date1, date2, companyId);
        return events.stream().map(event -> modelMapper.map(event, EventsDto.class)).collect(Collectors.toList());
    }

    @Override
    public EventsDto addEvent(EventsDto eventsDto, String username, Long companyId) {
        Date inputDate = eventsDto.getDate();

        LocalDateTime localDateTime = inputDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .withHour(9)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        eventsDto.setDate(Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()));

        EventsEntity event = modelMapper.map(eventsDto, EventsEntity.class);
        event.setCreatedDate(new Date());
        event.setCreatedBy(username);
        event.setCompanyId(companyId);
        event = eventsRepository.save(event);
        //öğrenci fonksiyonu
        addStudentPayment(eventsDto.getStudentId(), event, eventsDto.getEventStatus(), username);
        //öğretmen fonksiyonu
        if (eventsDto.getPriceToTeacher()) {
            addTeacherPayment(eventsDto.getTeacherId(), event.getEventId(), eventsDto.getEventStatus(), username, event.getCompanyId());
        }
        return modelMapper.map(event, EventsDto.class);
    }

   // TODO düzenlenecek
    @Override
    public void addEventNew(EventsDto eventsDto, BigDecimal paymentAmount, String username, Long companyId) {
        EventsEntity event = modelMapper.map(eventsDto, EventsEntity.class);
        event.setCreatedDate(new Date());
        event.setCreatedBy(username);
        event.setCompanyId(companyId);
        event = eventsRepository.save(event);
        //öğrenci fonksiyonu
        addStudentPaymentNew(event.getEventId(), eventsDto.getEventStatus(), username, event.getCompanyId(), paymentAmount);
        //öğretmen fonksiyonu
        if (event.getTeacherId() != null) {
            event.setPriceToTeacher(true);
            addTeacherPayment(eventsDto.getTeacherId(), event.getEventId(), eventsDto.getEventStatus(), username, event.getCompanyId());
        }
        modelMapper.map(event, EventsDto.class);
    }

    @Override
    public EventsDto updateEvent(EventsDto eventsDto, String username) {
        Optional<EventsEntity> eventsEntity = eventsRepository.findById(eventsDto.getEventId());
        EventsEntity event = modelMapper.map(eventsDto, EventsEntity.class);
        event.setUpdatedDate(new Date());
        event.setUpdatedBy("Admin");
        event.setCompanyId(eventsEntity.get().getCompanyId());
        event.setCreatedBy(eventsEntity.get().getCreatedBy());
        event.setCreatedDate(eventsEntity.get().getCreatedDate());
        //updateStudent
        updatePayment(eventsDto.getEventId(), 0, eventsDto.getEventStatus(), username, eventsDto.getPriceToTeacher());
        //updateTeacher
        if (eventsDto.getPriceToTeacher().equals(true)) {
            updatePayment(eventsDto.getEventId(), 1, eventsDto.getEventStatus(), username, eventsDto.getPriceToTeacher());
        }

        return modelMapper.map(eventsRepository.save(event), EventsDto.class);
    }

    @Override
    public EventsDto addRoutineEvent(EventsDto eventsDto, Long repeatIntervalDays, String username, Long companyId) {
        EventsDto event = addEvent(eventsDto, username, companyId);

        ScheduledEventsDto scheduledEventsDto = new ScheduledEventsDto();
        scheduledEventsDto.setEventId(event.getEventId());
        scheduledEventsDto.setScheduledName(eventsDto.getTitle());
        scheduledEventsDto.setRepeatIntervalDays(repeatIntervalDays);
        scheduledEventsDto.setStartDate(event.getDate());
        //scheduledEventsService.saveScheduledEvent(scheduledEventsDto);

        return event;
    }

    @Override
    public List<EventWithPaymentDto> getEventsByStudentId(Long studentId, Long seasonId, Integer month) {
        List<Date> monthToDate = monthToDate(month + 1, seasonId);
        List<EventsEntity> events = eventsRepository.findByDateBetweenAndStudentIdAndEventStatus(monthToDate.get(0),
                monthToDate.get(1), studentId, true);

        return events.stream().map(event -> {
            // EventsDto oluştur
            EventsDto eventDto = modelMapper.map(event, EventsDto.class);

            // PaymentDto bilgisi al
            PaymentDto paymentDto = paymentService.getPaymentByEventId(event.getEventId(), 0);
            if (paymentDto == null) {
                paymentDto = paymentService.getPaymentByEventId(event.getEventId(), 1);
            }

            // EventWithPaymentDto oluştur
            EventWithPaymentDto eventWithPaymentDto = new EventWithPaymentDto();
            eventWithPaymentDto.setEvent(eventDto);
            eventWithPaymentDto.setPayment(paymentDto);

            return eventWithPaymentDto;
        }).collect(Collectors.toList());
    }


    @Override
    public List<EventWithPaymentDto> getEventsByTeacherId(Long teacherId, Long seasonId, Integer month) {
        List<Date> monthToDate = monthToDate(month + 1, seasonId);
        List<EventsEntity> events = eventsRepository.findByDateBetweenAndTeacherIdAndEventStatusAndPriceToTeacher(monthToDate.get(0),
                monthToDate.get(1), teacherId, true, true);

        return events.stream().map(event -> {
            EventsDto eventDto = modelMapper.map(event, EventsDto.class);
            PaymentDto paymentDto = paymentService.getPaymentByEventId(event.getEventId(), 1);

            EventWithPaymentDto eventWithPaymentDto = new EventWithPaymentDto();
            eventWithPaymentDto.setEvent(eventDto);
            eventWithPaymentDto.setPayment(paymentDto);

            return eventWithPaymentDto;
        }).collect(Collectors.toList());
    }

    @Override
    public EventsDto getLastEventByStudentId(Long studentId) {
        EventsEntity event = eventsRepository.findTopByStudentIdAndEventStatusOrderByDateDesc(studentId, true);

        if (event == null) {
            event = new EventsEntity();
            event.setStudentId(studentId);
        }

        return modelMapper.map(event, EventsDto.class);
    }

    @Override
    public EventsDto getLastEventByTeacherId(Long teacherId) {
        EventsEntity event = eventsRepository.findTopByTeacherIdAndEventStatusOrderByDateDesc(teacherId, true);
        return modelMapper.map(event, EventsDto.class);
    }

    @Override
    public Long getSizeOfEvents(Long companyId) {
        List<Date> monthToDate = monthToDate(LocalDate.now().getMonthValue());
        return eventsRepository.countByCompanyIdAndEventStatusAndDateBetween(companyId, true, monthToDate.get(0), monthToDate.get(1));
    }

    private void addStudentPayment(Long studentId, EventsEntity event, Boolean eventStatus, String username) {
        Optional<StudentsEntity> studentsEntity = studentsRepository.findById(studentId);
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setEventId(event.getEventId());
        paymentDto.setPaymentType(0);
        studentsEntity.ifPresent(students -> students.getEnrollments().forEach(enrollmentEntity -> {
            if (Objects.equals(enrollmentEntity.getLesson().getLessonId(), event.getLessonId())) {
                paymentDto.setPaymentAmount(enrollmentEntity.getPrice().longValue());
                paymentDto.setRemainingAmount(enrollmentEntity.getPrice().longValue());
            }
        }));
        paymentDto.setAmountReceived(0L);
        //paymentDto.setPaymentAmount(studentsEntity.getLesPrice());
        checkEventStatus(eventStatus, paymentDto, username, event.getCompanyId());
    }

// TODO düzenlenecek
    private void addStudentPaymentNew(Long eventId, Boolean eventStatus, String username, Long companyId, BigDecimal paymentAmount) {
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setEventId(eventId);
        paymentDto.setPaymentType(0);
        paymentDto.setPaymentAmount(paymentAmount.longValue());
        paymentDto.setRemainingAmount(paymentAmount.longValue());
        paymentDto.setAmountReceived(0L);
        checkEventStatus(eventStatus, paymentDto, username, companyId);
    }

    public void addTeacherPayment(Long teacherId, Long eventId, Boolean eventStatus, String username, Long companyId) {
        TeacherEntity teacherEntity = teachersService.getTeacher(teacherId);
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setEventId(eventId);
        paymentDto.setPaymentType(1);
        paymentDto.setPaymentAmount(teacherEntity.getTeacherLesPrice());
        paymentDto.setRemainingAmount(teacherEntity.getTeacherLesPrice());
        paymentDto.setAmountReceived(0L);
        checkEventStatus(eventStatus, paymentDto, username, companyId);
    }

    public void addTeacherPayment(EventsDto eventsDto, TeacherEntity teacher, String username, Long companyId) {
        EventsEntity event = modelMapper.map(eventsDto, EventsEntity.class);
        event.setCreatedDate(new Date());
        event.setCreatedBy(username);
        event.setCompanyId(companyId);
        event = eventsRepository.save(event);

        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setPaymentAmount(teacher.getTeacherBaseFee());
        paymentDto.setRemainingAmount(teacher.getTeacherBaseFee());
        paymentDto.setAmountReceived(0L);
        paymentDto.setEventId(event.getEventId());
        paymentDto.setPaymentType(1);

        checkEventStatus(true, paymentDto, username, companyId);
    }

    private void checkEventStatus(Boolean eventStatus, PaymentDto paymentDto, String username, Long companyId) {
        if (eventStatus){
            paymentDto.setPaymentStatus(0);
        } else {
            paymentDto.setPaymentStatus(2);
        }
        PaymentEntity payment = modelMapper.map(paymentDto, PaymentEntity.class);
        payment.setCreatedBy(username);
        payment.setCreatedDate(new Date());
        payment.setCompanyId(companyId);
        paymentRepository.save(payment);
    }

    private void updatePayment(Long eventId, Integer paymentType, Boolean eventStatus, String username, Boolean priceToTeacher) {
        PaymentEntity payment = paymentRepository.findByEventIdAndPaymentType(eventId, paymentType);
        if (eventStatus){
            payment.setPaymentStatus(0);
        } else {
            payment.setPaymentStatus(2);
        }
        payment.setUpdatedBy(username);
        payment.setUpdatedDate(new Date());
        paymentRepository.save(payment);
    }

    @Override
    public List<Date> monthToDate(Integer month, Long seasonId) {
        Optional<SeasonsEntity> season = seasonsRepository.findById(seasonId);

        LocalDate ilkGun;
        LocalDate sonGun;

        if (season.isPresent()) {
            SeasonsEntity thisSeason = season.get();
            int year;

            if (month >= 8) {
                year = thisSeason.getStartDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .getYear();
            } else {
                year = thisSeason.getEndDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .getYear();
            }

            ilkGun = LocalDate.of(year, month, 1);
        } else {
            ilkGun = LocalDate.of(LocalDate.now().getYear(), month, 1);
        }
        sonGun = ilkGun.plusDays(ilkGun.getMonth().length(ilkGun.isLeapYear()) - 1);

        List<Date> dates = new ArrayList<>();
        dates.add(Date.from(ilkGun.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        dates.add(Date.from(sonGun.atStartOfDay(ZoneId.systemDefault()).toInstant()));

        return dates;
    }

    @Override
    public List<Date> monthToDate(Integer month) {
        LocalDate ilkGun = LocalDate.of(LocalDate.now().getYear(), month, 1);

        LocalDate sonGun = ilkGun.plusDays(ilkGun.getMonth().length(ilkGun.isLeapYear()) - 1);

        List<Date> tarihler = new ArrayList<>();
        tarihler.add(Date.from(ilkGun.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        tarihler.add(Date.from(sonGun.atStartOfDay(ZoneId.systemDefault()).toInstant()));

        return tarihler;
    }
}
