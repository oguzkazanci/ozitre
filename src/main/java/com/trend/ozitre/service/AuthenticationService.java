package com.trend.ozitre.service;

import com.trend.ozitre.dto.response.MessageResponse;
import com.trend.ozitre.dto.UserDto;
import com.trend.ozitre.dto.request.UserRequest;
import com.trend.ozitre.dto.response.UserResponse;

public interface AuthenticationService {

    public UserResponse authenticateUser(UserRequest userDto);

    public MessageResponse registerUser(UserDto userDto);
}
