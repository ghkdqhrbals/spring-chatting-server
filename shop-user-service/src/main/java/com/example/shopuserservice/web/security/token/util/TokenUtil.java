package com.example.shopuserservice.web.security.token.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;

public class TokenUtil {
    public static String getRefreshToken(ServerHttpRequest request) {
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        HttpCookie accessToken = cookies.getFirst("refreshToken");
        if (accessToken != null) {
            return accessToken.getValue();
        } else {
            return null;
        }
    }

    /**
     * get access token from cookie
     * @param request
     * @return accessToken or {@code null}
     */
    public static String getAccessToken(ServerHttpRequest request) {
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        HttpCookie accessToken = cookies.getFirst("accessToken");
        if (accessToken != null) {
            return accessToken.getValue();
        } else {
            return null;
        }
    }
}
