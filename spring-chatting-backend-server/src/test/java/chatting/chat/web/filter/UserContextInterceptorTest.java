package chatting.chat.web.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import chatting.chat.web.sessionCluster.redis.UserRedisSessionRepository;
import chatting.chat.web.token.JwtTokenValidator;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
class UserContextInterceptorTest {
    @Mock
    private JwtTokenValidator jwtTokenValidator;

    @Mock
    private UserRedisSessionRepository userRedisSessionRepository;

    @InjectMocks
    private UserContextInterceptor interceptor;

    @Test
    @DisplayName("preHandle 은 유효한 refresh token 이 쿠키에 있을 때 true 를 반환합니다")
    void testPreHandle_WithValidRefreshToken_ShouldReturnTrue() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setCookies(new Cookie("refreshToken", "valid_token"));
        request.setRequestURI("/some-path");
        request.setMethod("GET");

        // when
        when(jwtTokenValidator.validateToken("valid_token")).thenReturn("user123");
        boolean result = interceptor.preHandle(request, response, null);

        // then
        assertTrue(result);
        assertThat(UserContext.getUserId()).isEqualTo("user123");
        verify(userRedisSessionRepository, never()).findById(anyString());
    }

    @Test
    @DisplayName("쿠키가 없으면 preHandle 은 false 를 반환하고 401 에러를 반환합니다")
    void whenMissingRefreshTokenCookie_ShouldReturnFalseAnd401Error() throws Exception {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI("/some-path");
        request.setMethod("GET");

        // When
        boolean result = interceptor.preHandle(request, response, null);

        // Then
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).isEqualTo("");
        assertThat(result).isFalse();
    }



}