package com.trend.ozitre.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Book_Progress")
@Data
public class BookProgressEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "book_progress_id")
    private Long bookProgressId;

    @Column(name = "given_book_id")
    private Long givenBookId;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "lesson_id")
    private Long lessonId;

    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "progress")
    private Long progress;
}
