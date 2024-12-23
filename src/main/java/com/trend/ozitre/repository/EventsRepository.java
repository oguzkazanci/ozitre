package com.trend.ozitre.repository;

import com.trend.ozitre.entity.EventsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventsRepository extends JpaRepository<EventsEntity,Long> {
    List<EventsEntity> findByEventStatusAndCompanyIdAndTitleIsNotLike(Boolean eventStatus, Long companyId, String title);
    List<EventsEntity> findTop7ByEventStatusAndCompanyId(Boolean eventStatus, Long companyId);
    List<EventsEntity> findByDateBetweenAndCompanyId(Date endDate, Date startDate, Long companyId);
    List<EventsEntity> findByDateBetweenAndStudentIdAndEventStatus(Date endDate, Date startDate, Long studentId, Boolean eventStatus);
    List<EventsEntity> findByDateBetweenAndTeacherIdAndEventStatusAndPriceToTeacher(Date endDate, Date startDate, Long teacherId,
                                                                                    Boolean eventStatus, Boolean priceToTeacher);
    EventsEntity findTopByStudentIdAndEventStatusOrderByDateDesc(Long studentId, Boolean eventStatus);
    EventsEntity findTopByTeacherIdAndEventStatusOrderByDateDesc(Long studentId, Boolean eventStatus);
    Long countByCompanyIdAndEventStatusAndDateBetween(Long companyId, Boolean eventStatus, Date startDate, Date endDate);
    Optional<EventsEntity> findByStudentIdAndDateAndEventStatusAndTitle(Long studentId, Date date, Boolean eventStatus, String title);

    Optional<EventsEntity> findByTeacherIdAndDateAndEventStatusAndTitle(Long teacherId, Date date, Boolean eventStatus, String title);
}
