package com.trend.ozitre.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class EventsDto {

    private Long eventId;
    private String title;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date date;
    private Long lessonId;
    private Long studentId;
    private Long teacherId;
    private Boolean eventStatus;
    private Boolean priceToTeacher;
    private String description;
}
