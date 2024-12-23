package com.trend.ozitre.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ScheduledEventsDto {

    private Long scheduledId;
    private Long eventId;
    private String scheduledName;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date startDate;
    private Long repeatIntervalDays;
}
