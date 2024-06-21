package com.chatservice.websocket.config;
import com.chatservice.repository.ChatRepository;
import com.chatservice.repository.UserRepository;
import com.chatservice.websocket.handler.UserListHandler;
import com.chatservice.websocket.handler.WebSocketHandler;
import com.chatservice.websocket.service.WebSocketSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatRepository chatRepository;

    private  final UserListHandler userListHandler;
    private final UserRepository userRepository;
    private final WebSocketSessionService sessionService;
    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Autowired
    public WebSocketConfig(ChatRepository chatRepository, UserRepository userRepository, UserListHandler userListHandler, WebSocketSessionService sessionService ) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.sessionService = sessionService;
        this.userListHandler = userListHandler;
    }

    /**
     * Registers a WebSocket handler with the WebSocketHandlerRegistry.
     * Creates a new instance of the WebSocketHandler class and sets the allowed origins for the WebSocket connection.
     *
     * @param registry The registry used to register WebSocket handlers.
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        logger.info("Registering WebSocket handlers...");
        registry.addHandler(new WebSocketHandler(chatRepository, userRepository,userListHandler), "/websocket-chat").setAllowedOrigins("*");

        registry.addHandler(new UserListHandler(userRepository, chatRepository,sessionService ), "/websocket-userlist").setAllowedOrigins("*");

    }


}
