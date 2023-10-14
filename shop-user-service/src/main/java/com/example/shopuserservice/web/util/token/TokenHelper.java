package com.example.shopuserservice.web.util.token;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class TokenHelper {

    public static String getUserIdFromCookie(HttpServletRequest request) {
        Arrays.stream(request.getCookies()).map(cookie -> {
            if (cookie.getName().equals("accessToken")) {
                return cookie.getValue();
            }
            return null;
        });

        return null;
    }
}
