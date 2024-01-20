package chatting.chat.web.token;

import chatting.chat.testutil.SecretGen;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.FactoryBasedNavigableListAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


public class JwtTokenValidatorTest {
    @InjectMocks
    private JwtTokenValidator jwtTokenValidator;
    private final String secret = SecretGen.generateRandomString(512);
    private final String otherSecret = SecretGen.generateRandomString(512);
    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("valid 한 토큰을 테스트 시 claim 일치 여부 확인")
    public void testValidToken() {

        // 유효한 토큰 생성
        String subject = "user123";
        String validToken = Jwts.builder()
            .setSubject(subject)
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact();

        jwtTokenValidator.secret = secret;

        String validatedSubject = jwtTokenValidator.validateToken(validToken);
        assertEquals(subject, validatedSubject);
    }

    @Test
    @DisplayName("만료된 토큰 테스트")
    public void testExpiredToken() {
        // given
        String subject = "user123";

        jwtTokenValidator.secret = secret;

        // when
        long expirationTimeMillis = System.currentTimeMillis() - 1000; // 현재 시간 이전으로 설정
        String expiredToken = Jwts.builder()
            .setSubject(subject)
            .setExpiration(new Date(expirationTimeMillis))
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact();

        // then
        String validatedSubject = jwtTokenValidator.validateToken(expiredToken);
        assertNull(validatedSubject);
    }

    @Test
    @DisplayName("잘못된 서명을 가진 토큰 테스트")
    public void testInvalidToken() {
        // 잘못된 서명을 가진 토큰 생성
        String subject = "user123";

        jwtTokenValidator.secret = secret;
        String invalidToken = Jwts.builder()
            .setSubject(subject)
            .signWith(SignatureAlgorithm.HS512, otherSecret)
            .compact();

        String validatedSubject = jwtTokenValidator.validateToken(invalidToken);
        assertThat(validatedSubject).isNull();
    }

}