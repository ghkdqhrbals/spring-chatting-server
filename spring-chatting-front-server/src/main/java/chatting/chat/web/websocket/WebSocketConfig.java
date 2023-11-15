package chatting.chat.web.websocket;

import chatting.chat.web.login.util.CookieUtil;
import java.security.Principal;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import java.util.*;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

// STOMP configuration
@Slf4j
@Configuration
@AllArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer  {
    private final WebSocketSessionManager webSocketSessionManager;

    // 메시지 발행 요청 : /pub (Application Destination Prefix)
    // 메시지 구독 요청 : /sub (enable Simple Broker)
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/pub");
        registry.enableSimpleBroker("/sub");
    }

    // STOMP의 WebSocket 엔드포인트 설정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp/chat")
            .addInterceptors(new HttpSessionHandshakeInterceptor() {
                @Override
                public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                    WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                    // read JWT from Cookie, and add it to WebSocket session
                    HttpServletRequest httpServletRequest = ((ServletServerHttpRequest) request).getServletRequest();
                    Cookie[] cookies = httpServletRequest.getCookies();

                    if (cookies != null) {
                        for (Cookie cookie : cookies) {
                            if (cookie.getName().equals("refreshToken")) {
                                log.info("refreshToken: {}", cookie.getValue());
                                attributes.put("refreshToken", cookie.getValue());
                            } else if (cookie.getName().equals("accessToken")) {
                                log.info("accessToken: {}", cookie.getValue());
                                attributes.put("accessToken", cookie.getValue());
                            }
                        }
                    }
                    return super.beforeHandshake(request, response, wsHandler, attributes);
                }

                @Override
                public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                    WebSocketHandler wsHandler, Exception ex) {
                    super.afterHandshake(request, response, wsHandler, ex);
                }
            })
                .setAllowedOrigins("http://localhost:8080")
                .withSockJS();
    }
}