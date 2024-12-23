package com.trend.ozitre.repository;

import com.trend.ozitre.entity.ScheduledEventsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface ScheduledEventsRepository extends JpaRepository<ScheduledEventsEntity, Long> {

    List<ScheduledEventsEntity> findByStartDateBefore(Date startDate);
}
