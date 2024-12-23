package com.trend.ozitre.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "Notification")
@Data
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "notification_id")
    private Long notificationId;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Column(name = "event_date", length = 20)
    private Date date;
    @Column(name = "type")
    private Long type;
    @Column(name = "username", length = 20)
    private String username;
    @Column(name = "scheduled_id")
    private Long scheduledId;
    @Column(name = "state")
    private Long state;
    @Column(name = "description")
    private String description;
}
