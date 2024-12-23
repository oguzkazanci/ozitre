package com.trend.ozitre.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Subjects")
@Data
public class SubjectEntity {

    @Id
    @SequenceGenerator(name = "subject_seq_gen", sequenceName = "subject_gen", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subject_seq_gen")
    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "lessons_id")
    private Long lessonId;

    @Column(name = "subject", length = 80)
    private String subject;

    @Column(name = "grade_id")
    private Long gradeId;

    @Column(name = "position", nullable = false)
    private Integer position;
}
