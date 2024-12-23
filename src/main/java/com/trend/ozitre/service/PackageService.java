package com.trend.ozitre.service;


import com.trend.ozitre.dto.PackageDto;

import java.util.List;

public interface PackageService {

    List<PackageDto> getPackages();

    PackageDto savePackage(PackageDto packageDto);

}
