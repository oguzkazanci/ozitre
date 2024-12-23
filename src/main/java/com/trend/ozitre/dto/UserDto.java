package com.trend.ozitre.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UserDto {

    private Long userId;
    private String username;
    private String password;
    private String userMail;
    private String firstName;
    private String lastName;
    private String userAddress;
    private String userPhoneNumber;
    private String userLastPassword;
    private Set<String> role;
}
