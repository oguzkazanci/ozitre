package com.trend.ozitre.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "Scheduled_Events")
@Data
public class ScheduledEventsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "scheduled_id")
    private Long scheduledId;
    @Column(name = "event_id")
    private Long eventId;
    @Column(name = "scheduled_name", length = 200)
    private String scheduledName;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Column(name = "start_date", length = 20)
    private Date startDate;
    @Column(name = "repeat_interval_days", length = 2)
    private Long repeatIntervalDays;

}
