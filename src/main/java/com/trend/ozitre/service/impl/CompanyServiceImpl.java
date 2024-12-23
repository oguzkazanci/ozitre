package com.trend.ozitre.service.impl;

import com.trend.ozitre.dto.CompanyDto;
import com.trend.ozitre.entity.CompanyEntity;
import com.trend.ozitre.repository.CompanyRepository;
import com.trend.ozitre.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    private final ModelMapper modelMapper;

    @Override
    public CompanyDto getCompanyDetails(Long companyId) {
        Optional<CompanyEntity> companyDetails = companyRepository.findById(companyId);

        if (companyDetails.isPresent()) {
            return modelMapper.map(companyDetails, CompanyDto.class);
        } else return null;
    }

    @Override
    public CompanyDto saveCompany(CompanyDto companyDto, String username) {
        Optional<CompanyEntity> companyEntity = null;
        if (companyDto.getCompanyId() != null) { companyEntity = companyRepository.findById(companyDto.getCompanyId()); }
        CompanyEntity company = modelMapper.map(companyDto, CompanyEntity.class);

        if (companyEntity != null) {
            company.setCreatedBy(companyEntity.get().getCreatedBy());
            company.setCreatedDate(companyEntity.get().getCreatedDate());
            company.setUpdatedDate(new Date());
            company.setUpdatedBy(username);
        } else {
            company.setCreatedDate(new Date());
            company.setCreatedBy(username);
        }

        return modelMapper.map(companyRepository.save(company), CompanyDto.class);
    }
}
