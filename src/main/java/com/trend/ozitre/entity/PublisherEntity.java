package com.trend.ozitre.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Publisher")
@Data
public class PublisherEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "publisher_id")
    private Long publisherId;
    @Column(name = "publisher_name", length = 100)
    private String publisherName;
}
