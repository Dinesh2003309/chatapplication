package com.chatservice.service;
import com.chatservice.payload.Response;
import jakarta.servlet.http.HttpServletRequest;

public interface ChatService {

    Response getRecipientsBySender(HttpServletRequest request);

    Response searchRecipientsBySender(HttpServletRequest request, String searchText);

    Response getChatHistory(HttpServletRequest request, Integer recipientId);

    Response getOnlineStatus(Integer userId);
}
