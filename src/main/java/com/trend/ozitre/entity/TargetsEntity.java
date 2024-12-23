package com.trend.ozitre.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Targets")
@Data
public class TargetsEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "target_id")
    private Long targetId;
    @Column(name = "target_type")
    private Long targetType;
    @Column(name = "target_status")
    private Long targetStatus;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Column(name = "target_date", length = 20)
    private Date targetDate;
    @Column(name = "student_id")
    private Long studentId;
    @Column(name = "lesson_id")
    private Long lessonId;
    @Column(name = "subject_id")
    private Long subjectId;
    @Column(name = "targeted_noq")
    private Long targetedNoQ;
    @Column(name = "solved_noq")
    private Long solvedNoQ;
    @Column(name = "study_time")
    private Long studyTimeinMin;
}
