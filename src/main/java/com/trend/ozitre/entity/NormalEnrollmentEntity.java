package com.trend.ozitre.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "Normal_Enrollment")
public class NormalEnrollmentEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nenrollment_id")
    private Long nEnrollmentId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private StudentsEntity student;

    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private LessonEntity lesson;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private TeacherEntity teacher;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "status", length = 1)
    private Integer status;

    @Column(name = "company_id")
    private Long companyId;
}
