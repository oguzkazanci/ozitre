package com.trend.ozitre.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Books")
@Data
public class LibraryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "book_id")
    private Long bookId;
    @Column(name = "book_name", length = 50)
    private String bookName;
    @Column(name = "lesson_id")
    private Long lessonId;
    @Column(name = "publisher_id")
    private Long publisherId;
    @Column(name = "grade_id")
    private Long gradeId;
}
