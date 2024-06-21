package com.dinesh.userservice.service;
import com.dinesh.userservice.Dto.ForgotResetPasswordDto;
import com.dinesh.userservice.Dto.UserDto;
import com.dinesh.userservice.model.LoginRequest;
import com.dinesh.userservice.payload.Response;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
	//SIGN UP
	Response signUp(UserDto request);

	//RESET PASSWORD
	Response resetPassword(ForgotResetPasswordDto request);

	//LOGIN
	Response login(LoginRequest request, HttpServletResponse servletResponse);


}
