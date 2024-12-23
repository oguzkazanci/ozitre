package com.trend.ozitre.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Grade")
@Data
public class GradeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "grade_id")
    private Long gradeId;
    @Column(name = "grade", length = 20)
    private String grade;
    @Column(name = "grade_type")
    private Integer gradeType;
}
