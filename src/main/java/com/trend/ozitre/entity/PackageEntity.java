package com.trend.ozitre.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Package")
@Data
public class PackageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long packageId;

    @Column(name = "name", length = 50)
    private String packageName;

    @ManyToMany
    @JoinTable(
            name = "package_lesson",
            joinColumns = @JoinColumn(name = "package_id"),
            inverseJoinColumns = @JoinColumn(name = "lesson_id")
    )
    private Set<LessonEntity> lessons = new HashSet<>();
}