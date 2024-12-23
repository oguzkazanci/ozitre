package com.trend.ozitre.service;

import com.trend.ozitre.dto.EventsDto;
import com.trend.ozitre.entity.EnrollmentEntity;
import com.trend.ozitre.entity.EventsEntity;
import com.trend.ozitre.repository.EnrollmentRepository;
import com.trend.ozitre.repository.EventsRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RepeatingEnrollmentService implements Job {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private EventsRepository eventsRepository;

    @Autowired
    private EventsService eventsService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        System.out.println("Özel Ders İçin Tekrarlı İşlem Başlatıldı!!!");
        List<EnrollmentEntity> recurringEnrollments = getRecurringEnrollmentsToProcess();
        System.out.println("recurringEnrollments size : " + recurringEnrollments.size());
        LocalDate today = LocalDate.now();
        LocalDateTime todayDT = today.atTime(LocalTime.of(10, 0));


        for (EnrollmentEntity enrollment : recurringEnrollments) {
            /*Optional<EventsEntity> event = eventsRepository.findById(lesson.getEventId());
            NotificationEntity notification = new NotificationEntity();
            notification.setScheduledId(lesson.getScheduledId());
            notification.setType(0L);
            notification.setUsername(event.get().getCreatedBy());
            notification.setDate(new Date());
            notification.setState(0L);
            notification.setDescription(lesson.getScheduledName());

            notificationRepository.save(notification);*/
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
            Optional<EventsEntity> optEvent = eventsRepository.findByStudentIdAndDateAndEventStatusAndTitle(event.getStudentId(),
                    event.getDate(), event.getEventStatus(), event.getTitle());
            if (optEvent.isEmpty()) {
                eventsService.addEventNew(event, enrollment.getPrice(), enrollment.getCreatedBy(), enrollment.getCompanyId());
            }
        }
    }

    public List<EnrollmentEntity> getRecurringEnrollmentsToProcess() {
        Date today = new Date();

        LocalDate tdLd = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE", Locale.forLanguageTag("tr"));
        String todayName = tdLd.format(formatter);

        return enrollmentRepository.getEnrollmentEntitiesByDays_NameAndFirstDateBeforeAndStatus(todayName, today, 0);
    }
}
