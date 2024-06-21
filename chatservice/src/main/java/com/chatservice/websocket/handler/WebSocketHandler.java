package com.chatservice.websocket.handler;
import com.chatservice.constants.Declarations;
import com.chatservice.model.Chat;
import com.chatservice.model.User;
import com.chatservice.repository.ChatRepository;
import com.chatservice.repository.UserRepository;
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
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static org.aspectj.weaver.model.AsmRelationshipUtils.MAX_MESSAGE_LENGTH;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketHandler.class);

    private final ChatRepository chatRepository;

    private final UserRepository userRepository;

    private final UserListHandler userListHandler;

    private final Map<String, WebSocketSession> sessionsMap = new ConcurrentHashMap<>();

    private final Map<String, Integer>unreadMessagesMap=new ConcurrentHashMap<>();

    @Autowired
    public WebSocketHandler(ChatRepository chatRepository, UserRepository userRepository, UserListHandler userListHandler) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.userListHandler = userListHandler;
    }

    /**
     * This method is called when a WebSocket connection is established. It handles the initialization of the connection,
     * checks the validity of the user and recipient IDs, updates the 'read' status of received messages, and counts the
     * number of unread messages for the connected user.
     *
     * @param session The WebSocket session object representing the connection.
     * @throws Exception if an error occurs during the execution of the method.
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        session.setTextMessageSizeLimit(128*1024);
        JSONObject socketId = getUserId(session);
        String userId = socketId.getString(Declarations.USERID);
        String recipientId = socketId.getString(Declarations.RECIPIENTID);
        // Add the session to the map with a unique key
        String sessionIdKey = generateSessionId(userId, recipientId);
        sessionsMap.put(sessionIdKey, session);
        // Update the 'read' status to true for messages received by the recipient
        // Asynchronously update read status
        CompletableFuture<Void> updateReadStatusFuture = CompletableFuture.runAsync(() -> updateReadStatus(userId, recipientId));
        // Check if the user and recipient exist, and handle invalid cases
        Optional<User> userOptional = userRepository.findById(Integer.parseInt(userId));
        Optional<User> recipientOptional = userRepository.findById(Integer.parseInt(recipientId));
        if (!userOptional.isPresent()) {
            LOGGER.info("Invalid userId: {}" ,userId);
            session.close(CloseStatus.NORMAL.withReason("Invalid userId"));
            return;
        }
        if (!recipientOptional.isPresent()) {
            LOGGER.info("Invalid recipientId: {}", recipientId);
            session.close(CloseStatus.NORMAL.withReason("Invalid recipientId"));
            return;
        }
        // Check if the sender and recipient IDs are the same
        if (userId.equals(recipientId)) {
            LOGGER.info("Sender ID and recipient ID are the same. Closing session.");
            session.close(CloseStatus.NORMAL.withReason("Sender ID and recipient ID are the same"));
            return;
        }
        LOGGER.info("WebSocket connection established with session ID: {}", session.getId());
        // Count the number of unread messages for the connected user
        int unreadCount = chatRepository.countByRecipientAndRead(Integer.parseInt(userId), false);
        unreadMessagesMap.put(userId, unreadCount);
        updateReadStatusFuture.thenRun(() -> userListHandler.updateSessionAfterNewMessage(userId));

    }
/**
 * Updates the read status of the unread chats between the sender and recipient.
 * Sets the 'read' flag to true for each chat in the list of unread chats.
 * Saves all the updated chats in the chat repository.
 *
 * @param userId      the ID of the recipient user
 * @param recipientId the ID of the sender user
 */
    private void updateReadStatus(String userId, String recipientId) {
        List<Chat> unreadChats = chatRepository.findBySenderAndRecipientAndRead(
                Integer.parseInt(recipientId), Integer.parseInt(userId), false);
        for (Chat chat : unreadChats) {
            chat.setRead(true);
        }
        chatRepository.saveAll(unreadChats);
    }

    /**
     * Handles the text messages received in a WebSocket session.
     * It checks if the sender ID exists in the user database, saves the message with the current timestamp,
     * updates the timestamp of the saved message, sends the message to the recipient and the session,
     * and updates the 'read' status of the messages received by the recipient in real-time.
     *
     * @param session  The WebSocket session in which the text message is received.
     * @param message  The text message received in the WebSocket session.
     * @throws Exception if an error occurs during message handling.
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JSONObject userId = getUserId(session);
        String senderId = userId.getString(Declarations.USERID);
        String recipientId = userId.getString(Declarations.RECIPIENTID);

        // Check if the senderId exists in the user database
        Optional<User> userCheck = userRepository.findById(Integer.parseInt(senderId));
        if (userCheck.isPresent()) {
            String payload = message.getPayload();
            LOGGER.info("Received message: {}", payload);
            if (session.isOpen() && !payload.trim().isEmpty()) {
                // Save the message with the current timestamp and get the saved message
                Chat savedChat = saveMessage(senderId, recipientId, payload);
                // Update the 'read' status to true for messages received by the recipient in real-time
                // Asynchronously update read status
                CompletableFuture.runAsync(() -> handleUpdateReadStatus(senderId, recipientId));
                // Send the new message to the session
                Integer id = savedChat.getId();
                sendMessageToOtherParticipant(senderId, recipientId, payload, id);
                sendMessageToSession(session, senderId, recipientId, payload, savedChat.getTimestamp(), id);

            }
            userListHandler.updateSessionAfterNewMessage(recipientId);
            userListHandler.updateSessionAfterNewMessage(senderId);
            LOGGER.info("chat message: updateSessionAfterNewMessage1 ===========> " + recipientId);
            LOGGER.info("chat message: updateSessionAfterNewMessage1 ===========> " + senderId);


        } else {
            LOGGER.info("Invalid userId: {}" , senderId);
            String errorMessage = "Invalid userId: " + senderId;
            session.sendMessage(new TextMessage(errorMessage));
            session.close(CloseStatus.NORMAL.withReason("Invalid userId"));
        }
    }
    /**
 * Updates the read status of the unread chats between the sender and recipient.
 * Sets the 'read' flag to true for each chat in the list of unread chats.
 * Saves all the updated chats in the chat repository.
 *
 * @param senderId    the ID of the sender
 * @param recipientId the ID of the recipient
 */
    private void handleUpdateReadStatus(String senderId, String recipientId) {
        List<Chat> unreadChats = chatRepository.findBySenderAndRecipientAndRead(
                Integer.parseInt(recipientId), Integer.parseInt(senderId), false);

        for (Chat chat : unreadChats) {
            chat.setRead(true);
        }
        chatRepository.saveAll(unreadChats);
    }

    /**
     * Removes a WebSocket session from the sessionsMap when the connection is closed.
     *
     * @param session The WebSocket session that was closed.
     * @param status The status of the WebSocket connection when it was closed.
     * @throws Exception if an error occurs during the execution of the method.
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Remove the session from the map when it is closed
        String sessionKey = getSessionKeyByValue(sessionsMap, session);
        if (sessionKey != null) {
            sessionsMap.remove(sessionKey);
            String[] userIds = sessionKey.split("_");
            if (userIds.length == 2) {
                String userId = userIds[0];
                LOGGER.info("WebSocket connection closed for userId: {}, reason: {}, status: {}", userId, status.getReason(), status.getCode());
            }
        }
    }

    /**
     * Extracts the user ID and recipient ID from the query parameters of a WebSocket session and returns them as a JSON object.
     *
     * @param session The WebSocket session from which to extract the user ID and recipient ID.
     * @return A JSON object containing the user ID and recipient ID extracted from the WebSocket session.
     */
    private JSONObject getUserId(WebSocketSession session) {
        String[] queryParam = session.getUri().getQuery().split("&");
        String userId = queryParam[0].split("=")[1];
        String recipientId = queryParam[1].split("=")[1];
        JSONObject getQueryValue = new JSONObject();
        getQueryValue.put(Declarations.USERID, userId);
        getQueryValue.put(Declarations.RECIPIENTID, recipientId);
        return getQueryValue;
    }

    /**
     * Saves a chat message to the database.
     *
     * @param senderId   The ID of the sender of the message.
     * @param content    The content of the message.
     */

    private Chat saveMessage(String senderId, String recipientId, String content) {
        Chat chat = new Chat();
        chat.setSender(Integer.parseInt(senderId));
        chat.setRecipient(Integer.parseInt(recipientId));
        chat.setContent(content);
        // Set the timestamp to the current time
        chat.setTimestamp(Instant.now());
        Optional<User> userCheck = userRepository.findById(Integer.parseInt(senderId));
        userCheck.ifPresent(chat::setUserId);
        // Save the message to the database and return the saved object
        return chatRepository.save(chat);
    }
    /**
     * Sends a message to the recipient of a chat conversation.
     * If the recipient is currently connected to the WebSocket session, the method updates the 'read' status of the messages received by the recipient and sends the message to their session.
     * If the recipient is not connected, the method sends a push notification to the recipient's device.
     *
     * @param senderId    The ID of the sender of the message.
     * @param recipientId The ID of the recipient of the message.
     * @param message     The content of the message.
     * @param id          The ID of the chat message.
     */
    private void sendMessageToOtherParticipant(String senderId, String recipientId, String message, Integer id) {
        String sessionKey = generateSessionId(senderId, recipientId);
        WebSocketSession senderSession = sessionsMap.get(sessionKey);
        sessionKey = generateSessionId(recipientId, senderId);
        WebSocketSession recipientSession = sessionsMap.get(sessionKey);
        String senderName = retrieveSenderName(senderId); // Implement this method to fetch sender's name

        if (recipientSession != null && recipientSession.isOpen()) {
            // Update the 'read' status to true for messages received by the recipient if the recipient is connected
            List<Chat> unreadChats = chatRepository.findBySenderAndRecipientAndRead(Integer.parseInt(senderId), Integer.parseInt(recipientId), false);
            for (Chat chat : unreadChats) {
                chat.setRead(true);
                chatRepository.save(chat);
            }
            sendMessageToSession(recipientSession, senderId, recipientId, message, Instant.now(), id);
        } else {
            String limitedMessage = message.substring(0, Math.min(message.length(), MAX_MESSAGE_LENGTH));
            System.out.println("notify user");
        }
    }
    private String retrieveSenderName(String senderId) {
         Optional<User> sender = userRepository.findById(Integer.parseInt(senderId));
         return sender.get().getFirstName();
    }

    /**
     * Sends a message to a specific WebSocket session by converting a Chat object to JSON and sending it as a TextMessage.
     *
     * @param session The WebSocket session to send the message to.
     * @param senderId The ID of the sender.
     * @param recipientId The ID of the recipient.
     * @param message The content of the message.
     * @param timestamp The timestamp of the message.
     * @param id The ID of the message.
     */

    private void sendMessageToSession(WebSocketSession session, String senderId, String recipientId, String message, Instant timestamp, Integer id) {
        try {
            String jsonMessage = chatRepository.findMessageById(id);
            session.sendMessage(new TextMessage(jsonMessage));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Generates a session ID by concatenating the user ID and recipient ID with an underscore.
     *
     * @param userId The ID of the user.
     * @param recipientId The ID of the recipient.
     * @return The generated session ID.
     */
    private String generateSessionId(String userId, String recipientId) {
        return userId + "_" + recipientId;
    }

    /**
     * Retrieves the key from a map based on the value of a WebSocketSession object.
     *
     * @param map The map containing the WebSocketSession objects.
     * @param session The WebSocketSession object for which to find the key.
     * @return The key corresponding to the given WebSocketSession object in the map. If no match is found, null is returned.
     */
    private String getSessionKeyByValue(Map<String, WebSocketSession> map, WebSocketSession session) {
        for (Map.Entry<String, WebSocketSession> entry : map.entrySet()) {
            if (Objects.equals(entry.getValue(), session)) {
                return entry.getKey();
            }
        }
        return null;
    }
}