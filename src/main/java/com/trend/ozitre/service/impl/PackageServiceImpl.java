package com.trend.ozitre.service.impl;

import com.trend.ozitre.dto.PackageDto;
import com.trend.ozitre.entity.PackageEntity;
import com.trend.ozitre.repository.PackageRepository;
import com.trend.ozitre.service.PackageService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PackageServiceImpl implements PackageService {

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<PackageDto> getPackages() {
        List<PackageEntity> events = packageRepository.findAll();
        return events.stream().map(event -> modelMapper.map(event, PackageDto.class)).collect(Collectors.toList());
    }

    @Override
    public PackageDto savePackage(PackageDto packageDto) {
        PackageEntity packageE = modelMapper.map(packageDto, PackageEntity.class);
        return modelMapper.map(packageRepository.save(packageE), PackageDto.class);
    }
}
