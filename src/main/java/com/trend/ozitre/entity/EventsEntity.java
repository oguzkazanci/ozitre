package com.trend.ozitre.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Events")
@Data
public class EventsEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "event_id")
    private Long eventId;
    @Column(name = "event_title", length = 200)
    private String title;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Column(name = "event_date", length = 20)
    private Date date;
    @Column(name = "lessons_id")
    private Long lessonId;
    @Column(name = "student_id")
    private Long studentId;
    @Column(name = "teacher_id")
    private Long teacherId;
    @Column(name= "event_status")
    private Boolean eventStatus = true;
    @Column(name= "price_to_teacher")
    private Boolean priceToTeacher = true;
    @Column(name = "description", length = 200)
    private String description;
    @Column(name = "company_id")
    private Long companyId;
}
