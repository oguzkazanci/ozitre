package com.trend.ozitre.repository;

import com.trend.ozitre.entity.SeasonsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface SeasonsRepository extends JpaRepository<SeasonsEntity, Long> {

    @Query("select s from SeasonsEntity s " +
            "where :dateParam between s.startDate and s.endDate")
    Optional<SeasonsEntity> findSeasonByDate(@Param("dateParam") Date dateParam);
}

