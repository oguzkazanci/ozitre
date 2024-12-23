package com.trend.ozitre.service;

import com.trend.ozitre.dto.UserDto;
import com.trend.ozitre.dto.response.BaseResponse;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {


    UserDto getUserDetails(String username);

    public String findUsername(String jwtToken);

    public boolean tokenControl(String jwtToken, UserDetails userDetails);

    public String generateToken(UserDetails user);

    BaseResponse changePassword(String oldPassword, String newPassword, String username);
}
