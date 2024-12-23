package com.trend.ozitre.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Table(name = "Lesson")
@Data
public class LessonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "lesson_id")
    private Long lessonId;

    @Column(name = "lesson", length = 20)
    private String lesson;

    @Column(name = "lesson_type")
    private Long lessonType;

    @Column(name = "book_type")
    private Long bookType;
}
