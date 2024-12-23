package com.trend.ozitre.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Teachers")
@Data
public class TeacherEntity extends BaseEntity {

    @Id
    @SequenceGenerator(name = "user_seq_gen", sequenceName = "user_gen", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_gen")
    @Column(name = "teacher_id")
    private Long teacherId;

    @Column(name = "username", length = 20)
    private String username;

    @Column(name = "teacher_state", length = 1, columnDefinition = "integer default 0")
    private Integer teacherState;

    @Column(name = "work_type", length = 1)
    private Integer workType;

    @Column(name = "teacher_mail", length = 100)
    private String teacherMail;

    @Column(name = "teacher_phone_number", length = 20)
    private String teacherPhoneNumber;

    @Column(name = "teacher_name", length = 40)
    private String teacherName;

    @Column(name = "teacher_surname", length = 40)
    private String teacherSurname;

    @Column(name = "teacher_address", length = 150)
    private String teacherAddress;

    @ManyToMany
    @JoinTable(
            name = "teacher_lessons",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "lesson_id")
    )
    private Set<LessonEntity> lessons = new HashSet<>();

    @Column(name = "teacher_les_price", length = 6)
    private Long teacherLesPrice;

    @Column(name = "base_fee", length = 6)
    private Long teacherBaseFee;

    @Column(name = "explanation", length = 200)
    private String explanation;

    @Column(name = "company_id")
    private Long companyId;
}
