package com.chatservice.websocket.service;


import org.springframework.web.socket.WebSocketSession;

public interface WebSocketSessionService {

    // Add session
    public void addSession(String userId, WebSocketSession session);

    // Get Session
    public WebSocketSession getSession(String userId);

    // Remove Session
    public void removeSession(String userId) ;
}

