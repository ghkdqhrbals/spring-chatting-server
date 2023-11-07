package com.example.shopuserservice.web.security;

import com.example.shopuserservice.web.security.token.UserRedisSession;
import com.example.shopuserservice.web.security.token.UserRedisSessionRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final String AUTHORITIES_KEY = "permissions";

    private final UserRedisSessionRepository userRedisSessionRepository;

    public JwtTokenProvider(UserRedisSessionRepository userRedisSessionRepository) {
        this.userRedisSessionRepository = userRedisSessionRepository;
    }

    @Value("${token.expiration_time}")
    String expirationTime;

    @Value("${token.refresh_expiration_time}")
    String refreshExpirationTime;

    @Value("${token.secret}")
    String secret;


    /***
     * jwt payload
     * {
     *   "sub": "userId",
     *   "permissions": ["ROLE_USER","ROLE_ADMIN"],
     *   "iat": 1680778900,
     *   "exp": 1680865300
     * }
     */
    public String createToken(Authentication authentication) {
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        // Claims = sub + expiration + role
        Claims claims = Jwts.claims().setSubject(username);
        if (authorities != null) {
            claims.put(AUTHORITIES_KEY
                    , authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")));
        }

        Long expirationTimeLong = Long.parseLong(expirationTime);
        final Date createdDate = new Date();
        final Date expirationDate = new Date(createdDate.getTime() + expirationTimeLong);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public String generateAccessToken(String userId, String role) {
        // 토큰 클레임 (payload) 설정
        Map<String, Object> claims = new HashMap<>();
        claims.put(AUTHORITIES_KEY, role);
        claims.put("sub", userId);

        // Access Token 생성
        long now = (new Date()).getTime();
        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + Integer.parseInt(expirationTime));
        return Jwts.builder()
                .setSubject(userId)
                .setClaims(claims)
                .setExpiration(accessTokenExpiresIn)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String generateRefreshToken(String userId, String role) {
        // 토큰 클레임 (payload) 설정
        Map<String, Object> claims = new HashMap<>();
        claims.put(AUTHORITIES_KEY, role);
        claims.put("sub", userId);

        // Access Token 생성
        long now = (new Date()).getTime();
        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + Integer.parseInt(refreshExpirationTime));
        return Jwts.builder()
                .setSubject(userId)
                .setClaims(claims)
                .setExpiration(accessTokenExpiresIn)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String createRefreshToken(Authentication authentication) {
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        // Claims = sub + expiration + role
        Claims claims = Jwts.claims().setSubject(username);
        if (authorities != null) {
            claims.put(AUTHORITIES_KEY
                    , authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")));
        }

        Long expirationTimeLong = Long.parseLong(refreshExpirationTime);
        final Date createdDate = new Date();
        final Date expirationDate = new Date(createdDate.getTime() + expirationTimeLong);
        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();

        userRedisSessionRepository.save(UserRedisSession.builder()
                .userId(username)
                .refreshToken(refreshToken)
                .build());

        return refreshToken;
    }

    public Authentication getAuthentication(String token) {

        Claims claims = Jwts.parserBuilder().setSigningKey(this.secret).build().parseClaimsJws(token).getBody();

        Object authoritiesClaim = claims.get(AUTHORITIES_KEY);

        // permission check
        Collection<? extends GrantedAuthority> authorities = authoritiesClaim == null ? AuthorityUtils.NO_AUTHORITIES
                : AuthorityUtils.commaSeparatedStringToAuthorityList(authoritiesClaim.toString());
        authorities.forEach(c->{
            log.info("JWT has these authorities={}",c.getAuthority());
        });
        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String token, ServerWebExchange exchange) {
        try {
            Jws<Claims> claims = Jwts
                    .parserBuilder().setSigningKey(this.secret).build()
                    .parseClaimsJws(token);
            //  parseClaimsJws will check expiration date. No need do here.
            log.debug("JWT Owner: {}",claims.getBody().getSubject());
            log.debug("JWT Expiration: {}", claims.getBody().getExpiration());
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("JWT Error: {}", e.getMessage());
        }
        return false;
    }

    // Get userId from Spring Security Context
    public static String getUserIdFromSpringSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return ((User) authentication.getPrincipal()).getUsername();
    }

    public boolean isRefreshTokenInRedis(String refreshToken) {
        Optional<UserRedisSession> findSession = userRedisSessionRepository.findById(refreshToken);
        return findSession.isPresent();
    }
}
