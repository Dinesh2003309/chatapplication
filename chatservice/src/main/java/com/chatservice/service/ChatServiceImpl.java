package com.chatservice.service;
import java.util.*;

import com.chatservice.constants.Declarations;
import com.chatservice.constants.ToastMessage;
import com.chatservice.errorhandler.UnhandlerException;
import com.chatservice.errorhandler.UserNotFoundException;
import com.chatservice.model.User;
import com.chatservice.payload.Response;
import com.chatservice.repository.ChatRepository;
import com.chatservice.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class  ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;

    private final UserRepository userRepository;

    /**
     * Retrieves the recipients of a user based on the user's ID.
     *
     * @param request The HTTP request object containing the user ID.
     * @return The response object containing the success message, status code, and the list of recipients.
     */
    public Response getRecipientsBySender(HttpServletRequest request) {
        try {
            Integer userId = (Integer) request.getAttribute(Declarations.USER_ID);
            Optional<User> user = userRepository.findById(userId);
            if(!user.isPresent()) {
                throw new UserNotFoundException(ToastMessage.USER_NOT_FOUND);
            }
            List<String> recipients = chatRepository.findRecipientsBySender(userId);
            List<Map<String, Object>> getRecipient = new ArrayList<>();
            recipients.forEach(list -> {
                Map<String, Object> chatMap = new JSONObject(list).toMap();
                Integer senderId = (Integer) chatMap.get("senderId");
                Integer recipientId = (Integer) chatMap.get("recipientId");
                Boolean lastRead = chatRepository.lastRead(senderId, recipientId);
                long countUnread = chatRepository.countUnread(senderId, recipientId);
                chatMap.put("lastRead", lastRead);
                chatMap.put("unreadCount", countUnread);
                getRecipient.add(chatMap);
            });
            return Response.builder().message("Recipients retrieved successfully for the ID: " + userId).success(true)
                    .status(200).data(getRecipient).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.builder().message(e.getMessage()).status(HttpStatus.BAD_REQUEST.value()).success(false).build();
        }
    }

    /**
     * Searches for recipients based on a given search text and the sender's ID.
     *
     * @param request The HTTP request object containing the user ID.
     * @param searchText The text to search for in the recipients' full names.
     * @return A response object containing the search result or an error message if no recipients are found.
     */
    public Response searchRecipientsBySender(HttpServletRequest request, String searchText) {
        try {
            Integer userId = (Integer) request.getAttribute(Declarations.USER_ID);
            Optional<User> user = userRepository.findById(userId);
            if(!user.isPresent()) {
                throw new UserNotFoundException(ToastMessage.USER_NOT_FOUND);
            }
            List<String> recipients = chatRepository.findRecipientsBySenderKey(userId, searchText);
            List<Map<String, Object>> getRecipient = new ArrayList<>();
            recipients.forEach(list -> {
                Map<String, Object> chatMap = new JSONObject(list).toMap();
                Integer senderId = (Integer) chatMap.get("senderId");
                Integer recipientId = (Integer) chatMap.get("recipientId");
                Boolean lastRead = chatRepository.lastRead(senderId, recipientId);
                long countUnread = chatRepository.countUnread(senderId, recipientId);
                chatMap.put("lastRead", lastRead);
                chatMap.put("unreadCount", countUnread);
                getRecipient.add(chatMap);
            });
            if(recipients.isEmpty()){
                return Response.builder().message("Search Text was not found ").status(404).success(false).build();

            }
            return Response.builder().message("Search result").success(true)
                    .status(200).data(getRecipient).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.builder().message(e.getMessage()).status(HttpStatus.BAD_REQUEST.value()).success(false).build();
        }
    }


    /**
     * Retrieves the chat history between a user and a recipient based on their IDs.
     *
     * @param request The HTTP request object containing the user ID.
     * @param recipientId The ID of the recipient.
     * @return A response object containing the chat history between the user and the recipient.
     */
    public Response getChatHistory(HttpServletRequest request, Integer recipientId) {
        try {
            Integer userId = (Integer) request.getAttribute(Declarations.USER_ID);
            Optional<User> user = userRepository.findById(userId);
            if(!user.isPresent()) {
                throw new UserNotFoundException(ToastMessage.USER_NOT_FOUND);
            }
                List<String> chatHistoryResponse = chatRepository.findChatHistory(userId, recipientId);
                List<Map<String, Object>> getHistory = new ArrayList<>();
                chatHistoryResponse.forEach(list -> getHistory.add(new JSONObject(list).toMap()));
                return Response.builder().message("Chat history retrieved successfully.").success(true)
                        .status(HttpStatus.OK.value()).data(getHistory).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.builder().message(e.getMessage()).status(HttpStatus.BAD_REQUEST.value()).success(false).build();
        }
    }

    /**
     * Retrieves the online status of a user based on the user's ID.
     *
     * @param userId The ID of the user.
     * @return A response object containing the online status of the user.
     * @throws UserNotFoundException If the user is not found.
     * @throws UnhandlerException If an error occurs while retrieving the online status.
     */
    @Override
    public  Response getOnlineStatus(Integer userId){
        try{
            Optional<User> user = userRepository.findById(userId);
            if(!user.isPresent()) {
                throw new UserNotFoundException(ToastMessage.USER_NOT_FOUND);
            }
            String onlineStatus = userRepository.findOnlineStatusByUserId(userId);
            return Response.builder().message("Online Status Retrieved Successfully.").success(true).status(HttpStatus.OK.value()).data(new JSONObject(onlineStatus).toMap()).build();
        }catch (Exception e){
            throw new UnhandlerException(e.getMessage()+ "error message");
        }
    }


}
