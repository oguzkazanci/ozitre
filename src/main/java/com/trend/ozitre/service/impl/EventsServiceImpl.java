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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
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
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        Date date1 = sdf.parse(startDate);
        Date date2 = sdf.parse(endDate);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date2);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        date2 = calendar.getTime();

        List<EventsEntity> events = eventsRepository.findByDateBetweenAndCompanyId(date1, date2, companyId);

        return events.stream()
                .map(event -> modelMapper.map(event, EventsDto.class))
                .collect(Collectors.toList());
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
        if (eventStatus) {
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
        if (eventStatus) {
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

        sonGun = ilkGun.withDayOfMonth(ilkGun.lengthOfMonth());

        List<Date> dates = new ArrayList<>();
        dates.add(Date.from(ilkGun.atStartOfDay(ZoneId.systemDefault()).toInstant()));

        LocalDateTime sonGunSonSaat = sonGun.atTime(23, 59, 59);
        dates.add(Date.from(sonGunSonSaat.atZone(ZoneId.systemDefault()).toInstant()));

        return dates;
    }


    @Override
    public List<Date> monthToDate(Integer month) {
        LocalDate ilkGun = LocalDate.of(LocalDate.now().getYear(), month, 1);

        LocalDate sonGun = ilkGun.plusDays(ilkGun.getMonth().length(ilkGun.isLeapYear()) - 1);

        List<Date> dates = new ArrayList<>();
        dates.add(Date.from(ilkGun.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        dates.add(Date.from(sonGun.atStartOfDay(ZoneId.systemDefault()).toInstant()));

        return dates;
    }

    @Transactional
    @Override
    public void scheduleInstallmentsForStudent(StudentsEntity student, String username) {
        if (student.getPackageId() == null || student.getInstallment() == null || student.getInstallment() <= 0) {
            return;
        }

        final int installmentCount = student.getInstallment();

        BigDecimal total = BigDecimal.valueOf(
                Optional.ofNullable(student.getTotalPrice()).orElse(0)
        );

        BigDecimal advance = BigDecimal.valueOf(
                Optional.ofNullable(student.getAdvancePrice()).orElse(0)
        );

        if (advance.compareTo(BigDecimal.ZERO) > 0) {
            createAdvanceEventIfNotExists(student, advance, username);
        }

        BigDecimal remaining = total.subtract(advance);
        if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        BigDecimal[] divRem = remaining.divideAndRemainder(BigDecimal.valueOf(installmentCount));
        BigDecimal base = divRem[0];
        BigDecimal remainder = divRem[1];

        LocalDate created = student.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int startYear = created.getYear();
        int startMonth = student.getStartMonth();

        LocalDate startDate = LocalDate.of(startYear, startMonth, 1)
                .with(java.time.temporal.TemporalAdjusters.firstDayOfMonth());

        for (int i = 0; i < installmentCount; i++) {
            LocalDate due = startDate.plusMonths(i);

            Date dueDateAt09 = Date.from(
                    due.atTime(9, 0, 0).atZone(ZoneId.systemDefault()).toInstant()
            );

            String title = "Paket Dersi Düzenli - Taksit " + (i + 1);

            Optional<EventsEntity> exists = eventsRepository
                    .findByStudentIdAndDateAndEventStatusAndTitle(student.getStudentId(), dueDateAt09, true, title);
            if (exists.isPresent()) {
                continue;
            }

            BigDecimal amount = base;
            if (remainder.compareTo(BigDecimal.ZERO) > 0) {
                amount = amount.add(BigDecimal.ONE);
                remainder = remainder.subtract(BigDecimal.ONE);
            }

            EventsDto dto = new EventsDto();
            dto.setTitle(title);
            dto.setDate(dueDateAt09);
            dto.setStudentId(student.getStudentId());
            dto.setEventStatus(true);
            dto.setPriceToTeacher(false);

            // Event + Payment (öğrenci) tek seferde
            addEventNew(dto, amount, username, student.getCompanyId());
        }
    }

    /**
     * Peşinat için tek seferlik event + payment oluşturur.
     * Aynı gün/saat/başlıkla varsa yeniden oluşturmaz.
     */
    private void createAdvanceEventIfNotExists(StudentsEntity student, BigDecimal advanceAmount, String username) {
        LocalDate createdLocal = student.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int startYear = createdLocal.getYear();
        int startMonth = Optional.ofNullable(student.getStartMonth()).orElse(LocalDate.now().getMonthValue());
        LocalDate firstDue = LocalDate.of(startYear, startMonth, 1);
        Date advanceDateAt09 = Date.from(firstDue.atTime(9, 0).atZone(ZoneId.systemDefault()).toInstant());

        String title = "Peşinat";
        Optional<EventsEntity> exists = eventsRepository
                .findByStudentIdAndDateAndEventStatusAndTitle(student.getStudentId(), advanceDateAt09, true, title);

        if (exists.isPresent()) return;

        EventsDto advanceEvent = new EventsDto();
        advanceEvent.setTitle(title);
        advanceEvent.setDate(advanceDateAt09);
        advanceEvent.setStudentId(student.getStudentId());
        advanceEvent.setEventStatus(true);
        advanceEvent.setPriceToTeacher(false);

        addEventNew(advanceEvent, advanceAmount, username, student.getCompanyId());
    }

    private List<PlanItem> buildPlannedSchedule(StudentsEntity s, boolean advanceDateNow) {
        List<PlanItem> plan = new ArrayList<>();
        if (s.getPackageId() == null || s.getInstallment() == null || s.getInstallment() <= 0) return plan;

        int installmentCount = s.getInstallment();
        BigDecimal total   = BigDecimal.valueOf(Optional.ofNullable(s.getTotalPrice()).orElse(0));
        BigDecimal advance = BigDecimal.valueOf(Optional.ofNullable(s.getAdvancePrice()).orElse(0));

        // Peşinat
        if (advance.compareTo(BigDecimal.ZERO) > 0) {
            Date advanceDate = new Date(); // güvenli tarafta yine "şimdi" bırakıyoruz; isterseniz başka strateji koyabilirsiniz
            plan.add(new PlanItem("Peşinat", advanceDate, advance));
        }

        // Kalanı taksitlere böl
        BigDecimal remaining = total.subtract(advance);
        if (remaining.compareTo(BigDecimal.ZERO) <= 0) return plan;

        BigDecimal[] divRem = remaining.divideAndRemainder(BigDecimal.valueOf(installmentCount));
        BigDecimal base = divRem[0];
        BigDecimal rem  = divRem[1];

        // Taksitlerin tarihleri (ayın 1'i 09:00 varsayımı)
        LocalDate created = s.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int startYear  = created.getYear();
        int startMonth = Optional.ofNullable(s.getStartMonth()).orElse(LocalDate.now().getMonthValue());
        LocalDate firstDue = LocalDate.of(startYear, startMonth, 1);

        for (int i = 0; i < installmentCount; i++) {
            LocalDate due = firstDue.plusMonths(i);
            Date at09 = Date.from(due.atTime(9,0).atZone(ZoneId.systemDefault()).toInstant());
            BigDecimal amt = base;
            if (rem.compareTo(BigDecimal.ZERO) > 0) {
                amt = amt.add(BigDecimal.ONE);
                rem = rem.subtract(BigDecimal.ONE);
            }
            plan.add(new PlanItem("Paket Dersi Düzenli - Taksit " + (i + 1), at09, amt));
        }

        return plan;
    }

    @Override
    @Transactional
    public void reconcilePaymentsForStudent(StudentsEntity student, String username, boolean planChanged, boolean advanceChanged) {
        if (!planChanged) return;

        List<PlanItem> plan = buildPlannedSchedule(student, /*advanceDateNow*/ true);

        List<EventsEntity> existingEvents = eventsRepository.findByStudentId(student.getStudentId());

        Map<String, EventsEntity> byTitle = new HashMap<>();
        for (EventsEntity ev : existingEvents) {
            if (Boolean.TRUE.equals(ev.getPriceToTeacher())) continue;
            String t = ev.getTitle() == null ? "" : ev.getTitle().trim();
            if ("Peşinat".equals(t) || t.startsWith("Paket Dersi Düzenli - Taksit ")) {
                byTitle.put(t, ev);
            }
        }

        Set<String> touched = new HashSet<>();

        for (PlanItem item : plan) {
            String title = item.getTitle();
            EventsEntity ev = byTitle.get(title);

            if (ev == null) {
                EventsDto dto = new EventsDto();
                dto.setTitle(title);
                dto.setDate(item.getDate());
                dto.setStudentId(student.getStudentId());
                dto.setEventStatus(true);
                dto.setPriceToTeacher(false);
                addEventNew(dto, item.getAmount(), username, student.getCompanyId());
            } else {
                boolean changed = false;

                if (!Objects.equals(ev.getDate(), item.getDate())) {
                    ev.setDate(item.getDate());
                    changed = true;
                }

                PaymentEntity p = paymentRepository.findByEventIdAndPaymentType(ev.getEventId(), 0);
                if (p != null) {
                    long newAmount = item.getAmount().longValue();
                    if (!Objects.equals(p.getPaymentAmount(), newAmount)) {
                        p.setPaymentAmount(newAmount);
                        long received = Optional.ofNullable(p.getAmountReceived()).orElse(0L);
                        long newRemaining = Math.max(newAmount - received, 0L);
                        p.setRemainingAmount(newRemaining);
                        p.setUpdatedBy(username);
                        p.setUpdatedDate(new Date());
                        paymentRepository.save(p);
                    }
                }

                if (changed) {
                    ev.setUpdatedBy(username);
                    ev.setUpdatedDate(new Date());
                    eventsRepository.save(ev);
                }
            }
            touched.add(title);
        }

        for (Map.Entry<String, EventsEntity> e : byTitle.entrySet()) {
            String title = e.getKey();
            if (touched.contains(title)) continue;

            EventsEntity ev = e.getValue();
            PaymentEntity p = paymentRepository.findByEventIdAndPaymentType(ev.getEventId(), 0);
            long received = p == null ? 0L : Optional.ofNullable(p.getAmountReceived()).orElse(0L);

            if (received == 0L) {
                if (p != null) {
                    p.setPaymentStatus(0);
                    p.setUpdatedBy(username);
                    p.setUpdatedDate(new Date());
                    paymentRepository.save(p);
                }
                ev.setEventStatus(true);
                ev.setUpdatedBy(username);
                ev.setUpdatedDate(new Date());
                eventsRepository.save(ev);
            }
        }
    }

    private Date trimToDay(Date d) {
        if (d == null) return null;
        LocalDate ld = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class PlanItem {
        private String title;
        private Date date;
        private BigDecimal amount;
    }
}
