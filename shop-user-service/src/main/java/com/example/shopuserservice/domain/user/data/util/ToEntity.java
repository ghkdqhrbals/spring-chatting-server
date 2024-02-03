package com.example.shopuserservice.domain.user.data.util;

import com.example.commondto.format.DateFormat;
import com.example.shopuserservice.domain.user.data.User;
import com.example.shopuserservice.web.vo.RequestUser;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ToEntity {
    private final PasswordEncoder pwe;

    public User createUserEntity(RequestUser request) {
        return User.builder()
            .userName(request.getUserName())
            .email(request.getEmail())
            .role(request.getRole())
            .userPw(pwe.encode(request.getUserPw()))
            .userId(request.getUserId())
            .loginDate(DateFormat.getCurrentTime())
            .logoutDate(DateFormat.getCurrentTime())
            .build();
    }

}
