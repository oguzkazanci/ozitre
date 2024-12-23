package com.trend.ozitre.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@Entity
@Table(name = "Exam_Progress")
@EqualsAndHashCode(callSuper = true)
public class ExamProgressEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "exam_id")
    private Long examId;

    @Column(name = "exam_name", nullable = false, length = 50)
    private String examName;

    @Column(name = "exam_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date examDate;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "turkish_d", nullable = true, length = 2)
    private Integer turkishD;

    @Column(name = "turkish_y", nullable = true, length = 2)
    private Integer turkishY;

    @Column(name = "turkish_n", nullable = true, length = 2)
    private Integer turkishN;

    @Column(name = "math_d", nullable = true, length = 2)
    private Integer mathD;

    @Column(name = "math_y", nullable = true, length = 2)
    private Integer mathY;

    @Column(name = "math_n", nullable = true, length = 2)
    private Integer mathN;

    @Column(name = "science_d", nullable = true, length = 2)
    private Integer scienceD;

    @Column(name = "science_y", nullable = true, length = 2)
    private Integer scienceY;

    @Column(name = "science_n", nullable = true, length = 2)
    private Integer scienceN;

    @Column(name = "social_d", nullable = true, length = 2)
    private Integer socialD;

    @Column(name = "social_y", nullable = true, length = 2)
    private Integer socialY;

    @Column(name = "social_n", nullable = true, length = 2)
    private Integer socialN;
}
