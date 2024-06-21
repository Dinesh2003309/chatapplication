package com.chatservice.repository;

import java.util.List;

import com.chatservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(value = "SELECT json_build_object('userId',u.id, 'onlineStatus',u.online_status ) " +
            "FROM users u  where u.id = ?1 ", nativeQuery = true)
    String findOnlineStatusByUserId(Integer userId);

    @Query(value = "SELECT * FROM users WHERE email =?1 ", nativeQuery = true)
    User findByEmail(String username);

}
