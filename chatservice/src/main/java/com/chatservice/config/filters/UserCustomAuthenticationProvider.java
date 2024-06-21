package com.chatservice.config.filters;

import com.chatservice.config.service.UserDetailsServices;
import com.chatservice.model.User;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Order(2)
public class UserCustomAuthenticationProvider implements AuthenticationProvider{


    private final UserDetailsServices userDetailsServices;

    private final PasswordEncoder passwordEncoder;

    /**
     * Authenticates a user based on their provided credentials.
     *
     * @param authentication The authentication object containing the user's credentials.
     * @return The authenticated Authentication object if the credentials are valid, or null if the credentials are not valid.
     * @throws AuthenticationException If an authentication error occurs.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        User user = this.userDetailsServices.loadUserByUsername(authentication.getName());

        if(user.getUsername().equals(authentication.getName()) && passwordEncoder.matches(authentication.getCredentials().toString(), user.getPassword())) {
            String username = authentication.getName();
            String password = authentication.getAuthorities().toString();
            return new UsernamePasswordAuthenticationToken(username, password, user.getAuthorities());
        }

        return null;
    }

    /**
     * Checks if the provided authentication object is supported by this authentication provider.
     *
     * @param authentication The authentication object to be checked.
     * @return true if the authentication object is of type UsernamePasswordAuthenticationToken, false otherwise.
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}


