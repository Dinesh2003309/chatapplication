package com.dinesh.userservice.config.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dinesh.userservice.model.User;
import com.dinesh.userservice.repository.UserRepository;


@Service
@RequiredArgsConstructor
public class UserDetailsServices implements UserDetailsService{

	private final UserRepository userRepository;

    /**
     * Retrieves a user from the UserRepository based on the provided username.
     * Throws a UsernameNotFoundException if no user is found.
     *
     * @param username The username of the user to be loaded.
     * @return The user object loaded from the UserRepository.
     * @throws UsernameNotFoundException If no user is found with the given username.
     */
    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if(user == null) {
            throw new UsernameNotFoundException("No User Found With The Given Username");
        }
        return  user;
    }

}

