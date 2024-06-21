package com.chatservice.config;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.chatservice.config.service.UserDetailsServices;
import com.chatservice.config.utils.UserJwtUtils;
import com.chatservice.constants.Declarations;
import com.chatservice.model.User;
import com.chatservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserAuthTokenFilter extends OncePerRequestFilter{

    private final UserJwtUtils jwtUtils;

    private final UserDetailsServices userDetailsService;

    private final UserRepository userRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAuthTokenFilter.class);

    /**
     * Filters and processes incoming HTTP requests.
     *
     * This method checks if the request contains a valid JWT token in the Authorization header. If a valid token is found, it validates the token, retrieves the user details from the token, and sets the authentication context for the user. It also handles Cross-Origin Resource Sharing (CORS) by allowing specific origins to access the resources and setting the necessary headers.
     *
     * @param request The incoming HTTP request.
     * @param response The HTTP response.
     * @param filterChain The filter chain for processing the request.
     * @throws ServletException If an error occurs during the filtering process.
     * @throws IOException If an I/O error occurs during the filtering process.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String jwt = authorizationHeader.substring(7); // Remove "Bearer " prefix

                if (jwtUtils.validateJwtToken(jwt)) {
                    String username = jwtUtils.getUserNameFromJwtToken(jwt);

                    User userDetails = userDetailsService.loadUserByUsername(username);
                    request.setAttribute(Declarations.USER_ID, userDetails.getId());
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            LOGGER.error("UserAuth Filter error -----------------------> {} " , e.getMessage());
        }
        setCorsHeaders(request, response);
        filterChain.doFilter(request, response);
    }

    public void setCorsHeaders(HttpServletRequest request, HttpServletResponse response){
        List<String> allowOrigin = Arrays.asList("http://localhost:8110");
        String origin = request.getHeader("origin");
        if (allowOrigin.contains(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        }

        response.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS, PATCH");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Max-Age", "3600");
    }


}

