package com.trend.ozitre.service;

import com.trend.ozitre.dto.NotificationDto;
import com.trend.ozitre.dto.ScheduledEventsDto;

import java.util.List;

public interface NotificationService {

    List<NotificationDto> getUserUnreadNotifications(String username);

    Long getUserUnreadNotificationsSize(String username);

    Boolean saveNotification(NotificationDto notificationDto, Long companyId);
}
