package com.trend.ozitre.service;

import com.trend.ozitre.dto.EventsDto;
import com.trend.ozitre.entity.EnrollmentEntity;
import com.trend.ozitre.entity.EventsEntity;
import com.trend.ozitre.repository.EnrollmentRepository;
import com.trend.ozitre.repository.EventsRepository;
import com.trend.ozitre.repository.SeasonsRepository;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RepeatingEnrollmentService implements Job {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private EventsRepository eventsRepository;

    @Autowired
    private SeasonsRepository seasonsRepository;

    @Autowired
    private EventsService eventsService;

    @Override
    @Transactional
    public void execute(JobExecutionContext ctx) {
        System.out.println("Özel Ders İçin Tekrarlı İşlem Başlatıldı!!!");

        Date now = new Date();
        var currentSeasonOpt = seasonsRepository.findSeasonByDate(now);
        if (currentSeasonOpt.isEmpty()) {
            System.out.println("Uyarı: Bugüne karşılık gelen bir sezon bulunamadı. İşlem yapılmadı.");
            return;
        }
        var currentSeason = currentSeasonOpt.get();

        List<EnrollmentEntity> recurring = getRecurringEnrollmentsToProcess();
        System.out.println("recurringEnrollments size : " + recurring.size());

        LocalDate today = LocalDate.now();
        LocalDateTime todayDT = today.atTime(LocalTime.of(10, 0));

        List<Long> toCancelIds = new ArrayList<>();
        for (EnrollmentEntity enrollment : recurring) {
            Date created = enrollment.getCreatedDate();
            boolean createdInSeason =
                    !created.before(currentSeason.getStartDate()) &&
                            !created.after(currentSeason.getEndDate());

            if (!createdInSeason) {
                toCancelIds.add(enrollment.getEnrollmentId());
                continue;
            }

            EventsDto event = new EventsDto();
            event.setDate(Date.from(todayDT.atZone(ZoneId.systemDefault()).toInstant()));
            event.setLessonId(enrollment.getLesson().getLessonId());
            event.setTeacherId(enrollment.getTeacher().getTeacherId());
            event.setStudentId(enrollment.getStudent().getStudentId());

            String studentDisplayName = enrollment.getStudent().getName() + " " + enrollment.getStudent().getSurname();
            String teacherName = enrollment.getTeacher().getTeacherName();
            String lessonName = enrollment.getLesson().getLesson();
            event.setTitle(studentDisplayName + " - " + teacherName + " Hoca ile " + lessonName + " dersi");
            event.setEventStatus(true);
            event.setPriceToTeacher(false);

            Optional<EventsEntity> optEvent =
                    eventsRepository.findByStudentIdAndDateAndEventStatusAndTitle(
                            event.getStudentId(), event.getDate(), event.getEventStatus(), event.getTitle());

            if (optEvent.isEmpty()) {
                eventsService.addEventNew(event, enrollment.getPrice(), enrollment.getCreatedBy(), enrollment.getCompanyId());
            }
        }

        if (!toCancelIds.isEmpty()) {
            int updated = enrollmentRepository.cancelEnrollments(toCancelIds);
            System.out.println("Sezon dışı olduğu için iptal edilen enrollment sayısı: " + updated);
        }
    }

    public List<EnrollmentEntity> getRecurringEnrollmentsToProcess() {
        Date today = new Date();
        LocalDate ld = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEEE", Locale.forLanguageTag("tr"));
        String todayName = ld.format(fmt);

        return enrollmentRepository.getEnrollmentEntitiesByDays_NameAndFirstDateBeforeAndStatus(todayName, today, 0);
    }
}
