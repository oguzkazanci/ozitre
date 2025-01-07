package com.trend.ozitre.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "Seasons")
@Data
public class SeasonsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "season_id")
    private Long seasonId;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Column(name = "start_date", length = 20)
    private Date startDate;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Column(name = "end_date", length = 20)
    private Date endDate;
    @Column(name = "description", length = 10)
    private String description;
}
