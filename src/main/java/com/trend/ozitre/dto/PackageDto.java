package com.trend.ozitre.dto;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class PackageDto {

    private Long packageId;
    private String packageName;
    private Set<LessonDto> lessons = new HashSet<>();
}
