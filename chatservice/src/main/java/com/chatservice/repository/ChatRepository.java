package com.chatservice.repository;

import com.chatservice.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {

    @Query(value = "SELECT json_build_object('messageId', c.id, 'sender', c.sender, 'recipient', c.recipient, 'message', c.content, 'timestamp', c.timestamp, " +
            "'user', json_build_object('firstName', u.firstname, 'lastName', u.lastname, 'userid', u.id, 'phoneNo', u.phone_no)) " +
            "FROM chat c " +
            "JOIN users u ON (u.id = c.sender) " +
            "WHERE (c.sender = ?1 AND c.recipient = ?2) OR (c.recipient = ?1 AND c.sender = ?2) " +
            "ORDER BY c.timestamp", nativeQuery = true)
    List<String> findChatHistory(Integer sender, Integer recipient);

    @Query(value = "SELECT " +
            "json_build_object('messageId', c.id, 'sender', c.sender, 'recipient', c.recipient, 'message', c.content, " +
            "'timestamp', c.timestamp, 'user', json_build_object(" +
            "'firstName', u.firstname, 'lastName', u.lastname, 'userid', u.id, 'phoneNo', u.phone_no)) " +
            "FROM chat c " +
            "JOIN users u ON (u.id = c.sender) " +
            "WHERE c.id = ?1", nativeQuery = true)
    String findMessageById(Integer messageId);

    @Query(value = "SELECT * FROM chat WHERE (sender = ?1 AND recipient = ?2) OR (recipient = ?1 AND sender = ?2) ORDER BY timestamp", nativeQuery = true)
    List<Chat> findBySenderAndRecipientOrRecipientAndSenderOrderByTimestamp(Integer sender, Integer recipient, Integer recipient2, Integer sender2);

    @Query(value = "SELECT json_build_object('recipientId', u.id, 'firstName', u.firstname, 'lastName', u.lastname, 'email', u.email, 'fullName', CONCAT(u.firstname, ' ', u.lastname), " +
            "'onlineStatus',u.online_status ,'phoneNo', u.phone_no ,'senderId', ?1, 'lastMessage', c.content, 'lastTimestamp', c.timestamp ) " +
            "FROM users u " +
            "LEFT JOIN chat c ON (u.id = c.sender OR u.id = c.recipient) " +
            "WHERE (c.sender = ?1 OR c.recipient = ?1) AND c.timestamp = (SELECT MAX(timestamp) FROM chat WHERE (sender = u.id AND recipient = ?1) OR (sender = ?1 AND recipient = u.id)) " +
            "ORDER BY c.timestamp DESC", nativeQuery = true)
    List<String> findRecipientsBySender(Integer senderId);

    @Query(value = "SELECT json_build_object('recipientId', u.id, 'firstName', u.firstname, 'lastName', u.lastname, 'email', u.email, 'fullName', CONCAT(u.firstname, ' ', u.lastname), " +
            "'onlineStatus',u.online_status ,'phoneNo', u.phone_no ,'senderId', ?1, 'lastMessage', c.content, 'lastTimestamp', c.timestamp ) " +
            "FROM users u " +
            "LEFT JOIN chat c ON (u.id = c.sender OR u.id = c.recipient) " +
            "WHERE (c.sender = ?1 OR c.recipient = ?1) AND c.timestamp = (SELECT MAX(timestamp) FROM chat WHERE (sender = u.id AND recipient = ?1) OR (sender = ?1 AND recipient = u.id)) " +
            "AND CONCAT(u.firstname, ' ', u.lastname) ILIKE %?2% ORDER BY c.timestamp DESC", nativeQuery = true)
    List<String> findRecipientsBySenderKey(Integer senderId, String searchText);
    @Query(value = "SELECT CASE WHEN c.sender <> ?1 THEN c.read ELSE true END " +
            "FROM chat c " +
            "WHERE (c.sender = ?1 OR c.recipient = ?1) " +
            "AND c.timestamp = (SELECT MAX(timestamp) FROM chat WHERE (sender = ?1 AND recipient = ?2) OR (sender = ?2 AND recipient = ?1))", nativeQuery = true)
    Boolean lastRead(Integer senderId, Integer recipientId);

    @Query(value = " SELECT COUNT(*) FROM Chat WHERE sender = ?2 and recipient = ?1 and read = false ", nativeQuery = true)
    long countUnread(Integer senderId, Integer recipientId);

    //fetches messages from the database based on the sender, recipient, and read status.
    List<Chat> findBySenderAndRecipientAndRead(int senderId, int recipientId, boolean read);

    //counts the number of messages in the database based on the recipient and read status.
    int countByRecipientAndRead(int recipientId, boolean read);
}