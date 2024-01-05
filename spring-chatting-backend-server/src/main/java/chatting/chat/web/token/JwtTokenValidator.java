package chatting.chat.web.token;

import chatting.chat.web.sessionCluster.redis.UserRedisSessionRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Slf4j
@Component
public class JwtTokenValidator {
    private final String AUTHORITIES_KEY = "permissions";
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
     * Get Json-web-token from {@link HttpServletRequest} and validate it
     * @param token
     * @return true if token is valid, false if not
     */
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts
                .parserBuilder().setSigningKey(this.secret).build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.trace("JWT Error: {}", e.getMessage());
        }
        return false;
    }
}

