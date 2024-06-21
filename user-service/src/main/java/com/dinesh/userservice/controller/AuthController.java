package com.dinesh.userservice.controller;
import com.dinesh.userservice.Dto.ForgotResetPasswordDto;
import com.dinesh.userservice.Dto.UserDto;
import com.dinesh.userservice.model.LoginRequest;
import com.dinesh.userservice.payload.Response;
import com.dinesh.userservice.service.AuthService;
import com.dinesh.userservice.Dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(produces = "application/json", path = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;


    /**
     * Handles the user signup process.
     *
     * @param request The request object containing the user information for signup.
     * @return The response entity containing the HTTP status code and the response body.
     */
    @PostMapping(value = "/signup")  // User signup
    public ResponseEntity<Response> signUp(@Valid @RequestBody UserDto request) {
        Response response= authService.signUp(request);
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        return ResponseEntity.status(httpStatus).body(response);
    }


    /**
     * Handles the reset password functionality.
     *
     * @param request The ForgotResetPasswordDto object containing the necessary information for resetting the password.
     * @return The ResponseEntity object representing the result of the password reset operation.
     */
    @PostMapping(value = "/forgot/resetPassword") // Forgot password -> reset password
    public ResponseEntity<Response> resetPassword(@Valid @RequestBody ForgotResetPasswordDto request) {
        Response response = authService.resetPassword(request);
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        return ResponseEntity.status(httpStatus).body(response);
    }


    /**
     * Handles the login functionality for a user.
     * 
     * @param request The LoginRequest object containing the user's login credentials, device type, and FCM token.
     * @param servletResponse The servlet response object.
     * @return The ResponseEntity object containing the HTTP status code and the response body.
     */
    @PostMapping("/user/login") // User login
    public ResponseEntity<Response> login(@Valid @RequestBody LoginRequest request, HttpServletResponse servletResponse) {
        Response response = authService.login(request , servletResponse);
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        return ResponseEntity.status(httpStatus).body(response);
    }





}

