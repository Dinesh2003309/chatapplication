package com.chatservice.websocket.handler;

import com.chatservice.model.User;
import com.chatservice.repository.ChatRepository;
import com.chatservice.repository.UserRepository;
import com.chatservice.websocket.service.WebSocketSessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class UserListHandler  extends TextWebSocketHandler {
    private static final Logger LOG = LoggerFactory.getLogger(UserListHandler.class);

    private final UserRepository userRepository;

    private final ChatRepository chatRepository;

    private final WebSocketSessionService sessionService;

    @Autowired
    public UserListHandler(UserRepository userRepository, ChatRepository chatRepository, WebSocketSessionService sessionService){
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.sessionService = sessionService;
    }

    private final Map<String, WebSocketSession> userListSessionsMap = new ConcurrentHashMap<>();

    /**
     * Called when a new WebSocket connection is established.
     * Retrieves the user ID from the WebSocket session, adds the session to a map of active sessions,
     * and checks if the user exists in the database. If the user does not exist, the session is closed
     * with a reason of "Invalid userId". Otherwise, the user object is retrieved from the database.
     *
     * @param session The WebSocket session object representing the newly established connection.
     * @throws Exception if an error occurs during the execution of the method.
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = getUserId(session);
        LOG.info("user list : User Id from webSocket: {}" , userId);
        sessionService.addSession(userId, session);
        Optional<User> optionalUser = userRepository.findById(Integer.valueOf(userId));
        if (optionalUser.isEmpty()) {
            LOG.info("Invalid userId: {}" , userId);
            session.close(CloseStatus.NORMAL.withReason("Invalid userId"));
            return;
        }
        sendUserList(userId, session);
    }


    /**
     * Retrieves the user ID from the WebSocket session.
     *
     * @param session The WebSocket session object representing the connection.
     * @return The user ID extracted from the WebSocket session.
     */
    private String getUserId(WebSocketSession session) {
        String[] queryParam = session.getUri().getQuery().split("&");
        String userId = queryParam[0].split("=")[1];
        return userId;
    }


    /**
     * This method is called when a WebSocket connection is closed. It removes the session from the map of active sessions.
     *
     * @param session (WebSocketSession): The WebSocket session object representing the closed connection.
     * @param status (CloseStatus): The status of the connection close.
     * @throws Exception if an error occurs during the connection close handling.
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = getUserId(session);
        LOG.info("user list : Connection closed for user {}: {}", userId, status);
        sessionService.removeSession(userId);
    }


    public List<Map<String, Object>> sendUserList(String userId, WebSocketSession session) {
        List<Map<String, Object>> notificationsWithCounts = new ArrayList<>();

        List<String> unreadNotifications = chatRepository.findRecipientsBySender(Integer.valueOf(userId));
        unreadNotifications.forEach(list -> {
            Map<String, Object> notification = new JSONObject(list).toMap();
            Integer senderId = (Integer) notification.get("senderId");
            Integer recipientId = (Integer) notification.get("recipientId");
            Boolean lastRead = chatRepository.lastRead(senderId, recipientId);
            long countUnread = chatRepository.countUnread(senderId, recipientId);
            notification.put("lastRead", lastRead);
            notification.put("unreadCount", countUnread);
            notificationsWithCounts.add(notification);
        });

        List<Map<String, Object>> list = notificationsWithCounts.stream().collect(Collectors.toList());;

        if (!list.isEmpty()) {
            try {
                session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(list)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }


    public void updateSessionAfterNewMessage(String userId) {
            LOG.info("---->Message received in userHandler ");
            WebSocketSession session = sessionService.getSession(userId);

            if ( session != null && session.isOpen()) {
                System.out.println("msg received ..session is open===> in userHandler");
                sendUserList(userId,session);
            }
            else {
                LOG.info("Session {} is not open");
            }
    }
}
