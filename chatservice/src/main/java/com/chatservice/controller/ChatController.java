package com.chatservice.controller;
import com.chatservice.payload.Response;
import com.chatservice.service.ChatService;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class ChatController {
    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService=chatService;
    }

    /**
     * This method is used to get the recipients of a chat message based on the sender.
     * It returns a ResponseEntity object containing the recipients and their details.
     *
     * @param request The HTTP request object containing the sender's information.
     * @return The HTTP response containing the recipients and their details.
     */
    @GetMapping("/chat/list")
    public ResponseEntity<Response> getRecipientsBySender(HttpServletRequest request) {
        Response response = chatService.getRecipientsBySender(request);
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        return ResponseEntity.status(httpStatus).body(response);
    }

    /**
     * Searches for recipients of a chat message based on the sender's request and a search text.
     * 
     * @param request The HTTP request object containing the sender's information.
     * @param searchText The search text used to find recipients.
     * @return The HTTP response containing the search results.
     */
    @GetMapping("/chat/search")
    public ResponseEntity<Response> searchRecipientsBySender(HttpServletRequest request,
                                                             @RequestParam String searchText) {
        Response response= chatService.searchRecipientsBySender(request, searchText);
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        return ResponseEntity.status(httpStatus).body(response);
    }
    /**
     * Retrieves the chat history between the sender and a specific recipient.
     *
     * @param request The HTTP request object containing the sender's information.
     * @param recipientId The ID of the recipient for whom the chat history is being retrieved.
     * @return The HTTP response containing the chat history between the sender and the specified recipient.
     */
    @GetMapping("/chat/chatHistory")
    public ResponseEntity<Response> getChatHistory(HttpServletRequest request, @RequestParam("recipientId") Integer recipientId) {
        Response response = chatService.getChatHistory(request, recipientId);
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        return ResponseEntity.status(httpStatus).body(response);
    }

    /**
     * Retrieves the online status of a user based on their user ID.
     *
     * @param userId The ID of the user whose online status is being retrieved.
     * @return The HTTP response containing the user's online status.
     */
    @GetMapping("/chat/onlineStatus/{userId}")
    public ResponseEntity<Response> getOnlineStatus(@PathVariable Integer userId) {
        Response response= chatService.getOnlineStatus(userId);
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        return ResponseEntity.status(httpStatus).body(response);
    }

}