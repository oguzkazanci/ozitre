package com.trend.ozitre.dto;

import lombok.Data;

import java.util.Date;

@Data
public class NotificationDto {

    private Long notificationId;
    private Date date;
    private Long type;
    private String username;
    private Long scheduledId;
    private Long state;
    private String description;
}
