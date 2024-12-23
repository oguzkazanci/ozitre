package com.trend.ozitre.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Given_Books")
@Data
public class GivenBooksEntity extends BaseEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        @Column(name = "given_book_id")
        private Long givenBookId;
        @Column(name = "student_id")
        private Long studentId;
        @Column(name = "lesson_id")
        private Long lessonId;
        @Column(name = "book_id")
        private Long bookId;
        @Column(name = "grade_id")
        private Long gradeId;
        @Column(name = "publisher_id")
        private Long publisherId;
}
