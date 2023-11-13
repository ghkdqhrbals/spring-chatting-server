package chatting.chat.web.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionManager {
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    // WebSocket 세션을 저장
    public void addSession(String sessionId, WebSocketSession session) {
        sessionMap.put(sessionId, session);
    }

    // 세션 ID로 WebSocket 세션을 검색
    public WebSocketSession getSession(String sessionId) {
        return sessionMap.get(sessionId);
    }

    // WebSocket 세션을 삭제
    public void removeSession(String sessionId) {
        sessionMap.remove(sessionId);
    }
}
