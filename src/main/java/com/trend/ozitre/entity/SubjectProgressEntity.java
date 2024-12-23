package com.trend.ozitre.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "Subject_Progress")
@Data
@EqualsAndHashCode(callSuper = true)
public class SubjectProgressEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "subject_progress_id")
    private Long subjectProgressId;

    @Column(name = "foy_id", length = 2)
    private Integer foyId;

    @Column(name = "foy_kod", length = 10)
    private String foyKod;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "progress", length = 1)
    private Integer progress;
}
