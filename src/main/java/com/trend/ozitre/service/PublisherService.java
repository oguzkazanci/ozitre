package com.trend.ozitre.service;

import com.trend.ozitre.dto.PublisherDto;

import java.util.List;

public interface PublisherService {

    List<PublisherDto> getAllPublisher();

    PublisherDto savePublisher(PublisherDto publisherDto);

    Boolean removePublisher(Long publisherId);
}
