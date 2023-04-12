package com.example.shopuserservice.web.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final String AUTHORITIES_KEY = "permissions";

    @Value("${token.expiration_time}")
    String expirationTime;

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

    public Authentication getAuthentication(String token) {

        Claims claims = Jwts.parserBuilder().setSigningKey(this.secret).build().parseClaimsJws(token).getBody();

        Object authoritiesClaim = claims.get(AUTHORITIES_KEY);

        // 토큰에서 permission 체크
        Collection<? extends GrantedAuthority> authorities = authoritiesClaim == null ? AuthorityUtils.NO_AUTHORITIES
                : AuthorityUtils.commaSeparatedStringToAuthorityList(authoritiesClaim.toString());

        authorities.forEach(c->{
            log.info("권한획득: {}",c.toString());
        });

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts
                    .parserBuilder().setSigningKey(this.secret).build()
                    .parseClaimsJws(token);
            //  parseClaimsJws will check expiration date. No need do here.
            log.info("JWT 토큰 만료 시간: {}", claims.getBody().getExpiration());
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.info("JWT 토큰 사용 불가능: {}", e.getMessage());
            log.trace("TRACE", e);
        }
        return false;
    }
}
