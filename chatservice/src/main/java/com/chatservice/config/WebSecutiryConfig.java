package com.chatservice.config;

import com.chatservice.config.filters.UserCustomAuthenticationProvider;
import com.chatservice.enums.Permission;
import com.chatservice.enums.Roles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity
public class WebSecutiryConfig {

    private final ApplicationConfig applicationConfig;

    private final UserCustomAuthenticationProvider userCustomAuthenticationProvider;

    private final UserAuthTokenFilter userAuthenticationJwtTokenFilter;

    private final CustomAuthenticationEntryPoint unauthorizedHandler;

    /**
     * Creates and configures the authentication manager used for authentication in the application.
     *
     * @param http The HttpSecurity object used to configure the security settings.
     * @return The configured AuthenticationManager object.
     * @throws Exception if an error occurs during the configuration.
     */
    @Bean
    public AuthenticationManager authManager1(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http
                .getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(userCustomAuthenticationProvider);
        return authenticationManagerBuilder.build();
    }

    /**
     * Configures the security settings for the application by defining the authorization rules and adding filters for authentication.
     *
     * @param http The HttpSecurity object used to configure the security settings.
     * @return The configured SecurityFilterChain object.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/websocket-chat").permitAll()
                        .requestMatchers("/websocket-userlist").permitAll()
                        .requestMatchers("/swagger-ui/",
                                "/v2/api-docs",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-resources",
                                "/swagger-resources/**",
                                "/configuration/ui",
                                "/configuration/security",
                                "swagger-ui/**",
                                "/webjars/**",
                                "/swagger-ui/.html"
                        ).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll().requestMatchers("/api/v1/user/**")
                        .hasAnyRole(Roles.USER.name()).requestMatchers("/api/v1/admin/**")
                        .hasAnyRole(Roles.ADMIN.name()).requestMatchers(HttpMethod.GET, "/api/v1/admin/**")
                        .hasAnyAuthority(Permission.ADMIN_READ.name())
                        .requestMatchers(HttpMethod.POST, "/api/v1/admin/**")
                        .hasAnyAuthority(Permission.ADMIN_CREATE.name())
                        .requestMatchers(HttpMethod.PUT, "/api/v1/admin/**")
                        .hasAnyAuthority(Permission.ADMIN_UPDATE.name())
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/admin/**")
                        .hasAnyAuthority(Permission.ADMIN_DELETE.name())
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated());

        http.authenticationProvider(userCustomAuthenticationProvider);
        http.addFilterBefore(userAuthenticationJwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

