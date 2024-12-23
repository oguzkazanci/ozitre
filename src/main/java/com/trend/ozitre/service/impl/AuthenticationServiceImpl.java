package com.trend.ozitre.service.impl;

import com.trend.ozitre.dto.response.CompanyResponse;
import com.trend.ozitre.entity.RefreshTokenEntity;
import com.trend.ozitre.service.RefreshTokenService;
import com.trend.ozitre.util.JwtUtils;
import com.trend.ozitre.dto.response.MessageResponse;
import com.trend.ozitre.dto.UserDto;
import com.trend.ozitre.dto.request.UserRequest;
import com.trend.ozitre.dto.response.UserResponse;
import com.trend.ozitre.entity.RoleEntity;
import com.trend.ozitre.entity.UserEntity;
import com.trend.ozitre.enums.Role;
import com.trend.ozitre.repository.RoleRepository;
import com.trend.ozitre.repository.UserRepository;
import com.trend.ozitre.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final RefreshTokenService refreshTokenService;

    private final ModelMapper modelMapper;

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserResponse authenticateUser(UserRequest requestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(authentication);

        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        List<CompanyResponse> companies = userDetails.getCompanies();

        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return new UserResponse(jwt,
                userDetails.getId(),
                refreshToken.getToken(),
                userDetails.getUsername(),
                roles,
                companies);
    }

    @Override
    public MessageResponse registerUser(UserDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            return new MessageResponse("Error: Username is already taken!");
        }

        if (userRepository.existsByUserMail(userDto.getUserMail())) {
            return new MessageResponse("Error: Email is already in use!");
        }

        UserEntity user = modelMapper.map(userDto, UserEntity.class);

        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        Set<String> strRoles = userDto.getRole();
        Set<RoleEntity> roles = new HashSet<>();

        if (strRoles == null) {
            RoleEntity userRole = roleRepository.findByName(Role.STUDENT)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        RoleEntity adminRole = roleRepository.findByName(Role.ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "teacher":
                        RoleEntity teacherRole = roleRepository.findByName(Role.TEACHER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(teacherRole);
                        break;
                    case "manager":
                        RoleEntity managerRole = roleRepository.findByName(Role.MANAGER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(managerRole);
                        break;
                    case "coach":
                        RoleEntity coachRole = roleRepository.findByName(Role.COACH)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(coachRole);
                        break;
                    default:
                        RoleEntity userRole = roleRepository.findByName(Role.STUDENT)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return new MessageResponse("User registered successfully!");
    }
}
