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

/**
 * {@link JwtTokenProvider} class is used to create and validate Json Web Token.
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private static final String AUTHORITIES_KEY = "permissions";

    private final UserRedisSessionRepository userRedisSessionRepository;

    public JwtTokenProvider(UserRedisSessionRepository userRedisSessionRepository) {
        this.userRedisSessionRepository = userRedisSessionRepository;
    }

    /**
     * Access token's expiration time
     */
    @Value("${token.expiration_time}")
    String expirationTime;

    /**
     * Refresh token's expiration time
     */
    @Value("${token.refresh_expiration_time}")
    String refreshExpirationTime;

    @Value("${token.secret}")
    String secret;


    /**
     * Create Json Web Token with user's username and authorities
     *
     * <p> Here, this method generate accessToken </p>
     * <p> JWT's payload will looks like below</p>
     * <blockquote><pre>
     *  {
     *      "sub": "userId",
     *      "permissions": ["ROLE_USER","ROLE_ADMIN"],
     *      "iat": 1680778900,
     *      "exp": 1680865300
     *  }
     * </pre></blockquote>
     * @param authentication {@link Authentication}
     * @return Json-web-token
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

    /**
     * Create Json-web-token with user's username and authorities
     *
     * <p> Here, this method generate refreshToken. It has much longer expiration time than accessToken </p>
     * <p> After generate Token, save it to redis cluster session </p>
     * @param authentication {@link Authentication}
     * @return Json-web-token
     */
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

    /**
     * Get {@link Authentication} from Json-web-token
     * @param token
     * @return {@link Authentication}
     */
    public Authentication getAuthentication(String token) {

        Claims claims = Jwts.parserBuilder().setSigningKey(this.secret).build().parseClaimsJws(token).getBody();

        Object authoritiesClaim = claims.get(AUTHORITIES_KEY);

        // permission check
        Collection<? extends GrantedAuthority> authorities = authoritiesClaim == null ? AuthorityUtils.NO_AUTHORITIES
                : AuthorityUtils.commaSeparatedStringToAuthorityList(authoritiesClaim.toString());
        authorities.forEach(c->{
            log.debug("JWT has these authorities={}",c.getAuthority());
        });
        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    /**
     * Get Json-web-token from {@link HttpServletRequest} and validate it
     * @param token
     * @param exchange
     * @return true if token is valid, false if not
     */
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

    /**
     * Check {@link SecurityContextHolder} and get user's userId
     * @return user's userId
     */
    public static String getUserIdFromSpringSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return ((User) authentication.getPrincipal()).getUsername();
    }
}
