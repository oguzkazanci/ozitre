package com.trend.ozitre.service.impl;

import com.trend.ozitre.dto.UserDto;
import com.trend.ozitre.dto.response.BaseResponse;
import com.trend.ozitre.entity.UserEntity;
import com.trend.ozitre.enums.Role;
import com.trend.ozitre.repository.UserRepository;
import com.trend.ozitre.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${security.jwt.secret}")
    private String SECRET_KEY;

    private final ModelMapper modelMapper;

    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDto getUserDetails(String username) {
        Optional<UserEntity> userDetails = userRepository.findByUsername(username);

        if (userDetails.isPresent()) {
            return modelMapper.map(userDetails, UserDto.class);
        } else return null;
    }

    @Override
    public String findUsername(String jwtToken) {
        return exportToken(jwtToken, Claims::getSubject);
    }

    @Override
    public boolean tokenControl(String jwtToken, UserDetails userDetails) {
        final String username = findUsername(jwtToken);
        return (username.equals(userDetails.getUsername()) && !exportToken(jwtToken, Claims::getExpiration).before(new Date()));
    }

    @Override
    public String generateToken(UserDetails user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("Role", user.getAuthorities());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public BaseResponse changePassword(String oldPassword, String newPassword, String username) {
        Optional<UserEntity> userDetails = userRepository.findByUsername(username);
        BaseResponse response = new BaseResponse();

        if (userDetails.isPresent()) {
            if (!passwordEncoder.matches(newPassword, userDetails.get().getUserLastPassword())) {
                if (passwordEncoder.matches(oldPassword, userDetails.get().getPassword())) {
                    userDetails.get().setPassword(passwordEncoder.encode(newPassword));
                    userDetails.get().setUserLastPassword(passwordEncoder.encode(oldPassword));
                    userRepository.save(userDetails.get());
                    response.setMessage("Şifre Başarıyla Güncellendi!");
                    return response;
                } else {
                    response.setMessage("Şifre doğru değil!");
                    return response;
                }
            } else {
                response.setMessage("Yeni şifreniz son iki şifrenizle aynı olamaz.");
                return response;
            }
        } else {
            response.setMessage("Kullanıcı bulunmadı! Lütfen sistem yöneticinizle görüşünüz.");
            return response;
        }
    }

    private <T> T exportToken(String token, Function<Claims, T> claimsTFunction) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build().parseClaimsJws(token).getBody();

        return claimsTFunction.apply(claims);
    }

    private Key getKey() {
        byte[] key = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(key);
    }
}
