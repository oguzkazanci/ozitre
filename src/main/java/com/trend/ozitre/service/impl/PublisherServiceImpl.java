package com.trend.ozitre.service.impl;

import com.trend.ozitre.dto.PublisherDto;
import com.trend.ozitre.entity.PublisherEntity;
import com.trend.ozitre.repository.PublisherRepository;
import com.trend.ozitre.service.PublisherService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublisherServiceImpl implements PublisherService {

    private final PublisherRepository publisherRepository;

    private final ModelMapper modelMapper;

    @Override
    public List<PublisherDto> getAllPublisher() {
        List<PublisherEntity> publisherEntities = publisherRepository.findAll();
        return publisherEntities.stream().map(publisher -> modelMapper.map(publisher, PublisherDto.class)).collect(Collectors.toList());
    }

    @Override
    public PublisherDto savePublisher(PublisherDto publisherDto) {
        PublisherEntity publisher = modelMapper.map(publisherDto, PublisherEntity.class);
        return modelMapper.map(publisherRepository.save(publisher), PublisherDto.class);
    }

    @Override
    public Boolean removePublisher(Long publisherId) {
        Optional<PublisherEntity> publisher = publisherRepository.findById(publisherId);

        if(publisher.isPresent()) {
            publisherRepository.deleteById(publisherId);
            return true;
        }
        return false;
    }

}
