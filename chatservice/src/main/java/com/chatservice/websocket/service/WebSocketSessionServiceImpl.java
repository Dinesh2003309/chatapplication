package com.chatservice.websocket.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketSessionServiceImpl implements WebSocketSessionService{
    private final Map<String, WebSocketSession> sessionsMap = new ConcurrentHashMap<>();

    private static final Logger LOG = LoggerFactory.getLogger(WebSocketSessionServiceImpl.class);

    /**
     * Adds a WebSocket session to the sessionsMap.
     *
     * @param userId the ID of the user associated with the session
     * @param session the WebSocket session to be added
     */
    @Override
    public void addSession(String userId, WebSocketSession session) {
        LOG.info("Adding session to sessionsMap. Session ID: {} User ID:{} " ,session.getId(), userId);
        sessionsMap.put(userId, session);
    }

    /**
     * Retrieves the WebSocket session associated with the given user ID.
     *
     * @param userId the ID of the user
     * @return the WebSocket session associated with the user ID, or null if not found
     */
    @Override
    public WebSocketSession getSession(String userId) {
        LOG.info("SessionMap from getSession: {}" , sessionsMap);
        return sessionsMap.get(userId);
    }

    /**
     * Removes the WebSocket session associated with the given user ID from the sessionsMap.
     *
     * @param userId the ID of the user
     */
    @Override
    public void removeSession(String userId) {
        LOG.info("Removing session from sessionsMap. User ID: {}" , userId);
        sessionsMap.remove(userId);
    }

}
