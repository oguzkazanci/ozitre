package com.trend.ozitre.service.impl;

import com.trend.ozitre.dto.NotificationDto;
import com.trend.ozitre.entity.EventsEntity;
import com.trend.ozitre.entity.NotificationEntity;
import com.trend.ozitre.entity.ScheduledEventsEntity;
import com.trend.ozitre.repository.EventsRepository;
import com.trend.ozitre.repository.NotificationRepository;
import com.trend.ozitre.repository.ScheduledEventsRepository;
import com.trend.ozitre.service.EventsService;
import com.trend.ozitre.service.NotificationService;
import com.trend.ozitre.service.ScheduledEventsService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    private final EventsRepository eventsRepository;

    private final ScheduledEventsRepository scheduledEventsRepository;

    private final EventsService eventsService;

    private final ScheduledEventsService scheduledEventsService;

    private final ModelMapper modelMapper;

    @Override
    public List<NotificationDto> getUserUnreadNotifications(String username) {
        List<NotificationEntity> notifications = notificationRepository.findByUsernameAndState(username, 0L);
        return notifications.stream().map(notification -> modelMapper.map(notification, NotificationDto.class)).collect(Collectors.toList());
    }

    @Override
    public Long getUserUnreadNotificationsSize(String username) {
        return notificationRepository.countByUsernameAndState(username, 0L);
    }

    @Override
    public Boolean saveNotification(NotificationDto notificationDto, Long companyId) {
        ScheduledEventsEntity scheduledEvent = scheduledEventsRepository.getReferenceById(notificationDto.getScheduledId());
        EventsEntity event = eventsRepository.getReferenceById(scheduledEvent.getEventId());
        EventsEntity notiEvent = new EventsEntity();
        notiEvent.setStudentId(event.getStudentId());
        notiEvent.setCompanyId(companyId);

        return null;
    }
}
