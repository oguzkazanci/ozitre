package com.trend.ozitre.service;

import com.trend.ozitre.dto.EventsDto;
import com.trend.ozitre.entity.EventsEntity;
import com.trend.ozitre.entity.StudentsEntity;
import com.trend.ozitre.entity.TeacherEntity;
import com.trend.ozitre.repository.EventsRepository;
import com.trend.ozitre.repository.StudentsRepository;
import com.trend.ozitre.repository.TeachersRepository;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RepeatingPackageService implements Job {

    @Autowired
    private StudentsRepository studentsRepository;

    @Autowired
    private TeachersRepository teachersRepository;

    @Autowired
    private EventsService eventsService;

    @Autowired
    private EventsRepository eventsRepository;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        System.out.println("Paket Ders İçin Tekrarlı İşlem Başlatıldı!!!");
        List<StudentsEntity> studentsEntities = studentsRepository.findByPackageIdIsNotNull();
        List<TeacherEntity> teacherEntities = teachersRepository.findByTeacherBaseFeeIsNotNull();
        System.out.println("recurringPackage student size : " + studentsEntities.size());
        System.out.println("recurringPackage teacher size : " + teacherEntities.size());

        for (StudentsEntity student: studentsEntities) {
            int month = student.getStartMonth();
            LocalDate localDate = student.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate givenDate = LocalDate.of(localDate.getYear(), month, 1);
            LocalDate currentDate = LocalDate.now();

            if (currentDate.isAfter(givenDate)) {
                givenDate = LocalDate.of(currentDate.getYear(), currentDate.getMonthValue(), 1);
                EventsDto event = new EventsDto();
                event.setStudentId(student.getStudentId());
                event.setEventStatus(true);
                event.setDate(Date.from(givenDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                event.setTitle("Paket Dersi Düzenli");
                BigDecimal price = BigDecimal.valueOf(student.getTotalPrice() / student.getInstallment());
                Optional<EventsEntity> isEvent = eventsRepository.findByStudentIdAndDateAndEventStatusAndTitle(event.getStudentId(), event.getDate(), event.getEventStatus(), event.getTitle());
                if (isEvent.isEmpty()) {
                    eventsService.addEventNew(event, price, student.getCreatedBy(), student.getCompanyId());
                }
            }
        }

        for (TeacherEntity teacher: teacherEntities) {
            LocalDate currentDate = LocalDate.now();
            LocalDate givenDate = LocalDate.of(currentDate.getYear(), currentDate.getMonthValue(), 1);
            EventsDto event = new EventsDto();
            event.setTeacherId(teacher.getTeacherId());
            event.setEventStatus(true);
            event.setDate(Date.from(givenDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            event.setTitle("Öğretmen Düzenli Ödeme");
            Optional<EventsEntity> isEvent = eventsRepository.findByTeacherIdAndDateAndEventStatusAndTitle(event.getTeacherId(), event.getDate(), event.getEventStatus(), event.getTitle());
            if (isEvent.isEmpty()) {
                eventsService.addTeacherPayment(event, teacher, teacher.getCreatedBy(), teacher.getCompanyId());
            }
        }
    }
}
