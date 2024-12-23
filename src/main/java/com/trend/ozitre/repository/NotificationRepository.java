package com.trend.ozitre.repository;

import com.trend.ozitre.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findByUsernameAndState(String username, Long state);

    Long countByUsernameAndState(String username, Long state);
}
