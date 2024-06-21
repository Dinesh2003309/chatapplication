package com.dinesh.userservice.service;
import java.util.List;

import com.dinesh.userservice.Dto.ForgotResetPasswordDto;
import com.dinesh.userservice.Dto.UserDto;
import com.dinesh.userservice.constants.ToastMessage;
import com.dinesh.userservice.model.LoginRequest;
import com.dinesh.userservice.model.User;
import com.dinesh.userservice.repository.UserRepository;
import com.dinesh.userservice.model.*;
import com.dinesh.userservice.repository.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dinesh.userservice.config.utils.UserJwtUtils;
import com.dinesh.userservice.errorhandler.UnhandlerException;
import com.dinesh.userservice.errorhandler.ValidationException;
import com.dinesh.userservice.payload.Response;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

	private final PasswordEncoder passwordEncoder;

	private final UserRepository userRepository;

	private final UserJwtUtils userJwtUtils;


	/**
	 * Handles the sign-up functionality for new users.
	 *
	 * @param request The user's sign-up information including first name, last name, email, phone number, country, country code, zip code, device type, FCM token, and password.
	 * @return A response entity containing the response status, message, data (user information), and JWT token.
	 * @throws ValidationException If the provided phone number is invalid.
	 * @throws UnhandlerException If an unexpected error occurs.
	 */
	@Override
	public Response signUp(UserDto request) {
		try {
			// Check if the user already exists by phone number or email
			List<User> exist = userRepository.findUserByPhoneNoOrEmail(request.getPhoneNo(), request.getEmail());
			if (!exist.isEmpty()) {
				return Response.builder().message("Phone no. or email is already registered")
						.status(HttpStatus.CONFLICT.value()).success(false).build();
			}

			// Create a new user
			User newUser = User.builder()
					.firstName(request.getFirstName())
					.lastName(request.getLastName())
					.email(request.getEmail())
					.phoneNo(request.getPhoneNo())
					.password(passwordEncoder.encode(request.getPassword()))
					.build();

			userRepository.save(newUser);

			// Verify credentials and status of the new user
			User existUser = findByCredential(request.getEmail(), request.getPassword());
			if (existUser == null) {
				return Response.builder().message("Invalid Credentials")
						.status(HttpStatus.FORBIDDEN.value()).success(false).build();
			}

			// Generate JWT token for the new user
			String jwtToken = userJwtUtils.generateToken(newUser);

			// Fetch user information
			String userInfo = userRepository.findByUser(newUser.getId());
			JSONObject json = new JSONObject(userInfo);


			return Response.builder().message("User created successfully.")
					.status(HttpStatus.CREATED.value()).success(true).build();
		} catch (Exception e) {
			return Response.builder().message(e.getMessage()).status(HttpStatus.BAD_REQUEST.value()).build();
		}
	}


	/**
	 * Resets the password for a user.
	 *
	 * @param request The ForgotResetPasswordDto object containing the user's phone number and new password.
	 * @return A Response object indicating the result of the password reset operation.
	 * @throws UnhandlerException If an exception occurs during the password reset process.
	 */
	@Override
	public Response resetPassword(ForgotResetPasswordDto request) throws UnhandlerException {
		try {
			User user = userRepository.findUserByPhoneNo(request.getPhoneNo());
			if (user == null) {
				return Response.builder().status(HttpStatus.BAD_REQUEST.value()).success(false)
						.message(ToastMessage.PH_NO_DOESNT_MATCH).build();
			} else {
				if(passwordEncoder.matches(request.getNewPassword(), user.getPassword())){
					return Response.builder().message("New Password can't be old Password").status(HttpStatus.BAD_REQUEST.value()).success(false).build();
				}
				if (request.getNewPassword().equals(request.getConfirmPassword())) {
					user.setPassword(passwordEncoder.encode(request.getNewPassword()));
					userRepository.save(user);
					return Response.builder().status(HttpStatus.OK.value()).success(true)
							.message("Your password has been updated").build();
				} else {
					return Response.builder().status(HttpStatus.BAD_REQUEST.value()).success(false)
							.message("NewPassword & confirmPassword are not same").build();
				}
			}
		} catch (Exception e) {
			return Response.builder().status(HttpStatus.BAD_REQUEST.value()).success(false)
					.message(e.getMessage()).build();
		}
	}


	/**
	 * Authenticates the user login and generates a JWT token for the authenticated user.
	 *
	 * @param request           The login request object containing the user's email and password.
	 * @param servletResponse   The servlet response object.
	 * @return                  The response entity containing the response status, message, data, and JWT token.
	 */
	@Override
	public Response login(LoginRequest request, HttpServletResponse servletResponse) {
		try {
			User existUser = findByCredential(request.getUsername(), request.getPassword());
			if(existUser == null || !passwordEncoder.matches(request.getPassword(), existUser.getPassword())){
				return Response.builder().message("Invalid Credentials")
								.status(HttpStatus.FORBIDDEN.value()).success(false).build();
			}
			User user = userRepository.findByEmailOrPhoneNo(request.getUsername(), request.getUsername());
			String jwtToken = userJwtUtils.generateToken(user);
			String userInfo = userRepository.findByUser(user.getId());
			JSONObject json = new JSONObject(userInfo);
			if (existUser != null) {
				return Response.builder().data(json.toMap()).status(HttpStatus.OK.value())
						.message("Login Successfully").token(jwtToken).success(true).build();
			}
			else {
				return Response.builder().message("user Not Found, Please SignUp")
						.status(HttpStatus.BAD_REQUEST.value()).success(false).build();
			}		} catch (Exception e) {
			return Response.builder().message(e.getMessage())
					.status(HttpStatus.BAD_REQUEST.value()).success(false).build();
		}
	}
	/**
	 * Finds a user by their username (email) and password.
	 *
	 * @param username The username (email) of the user.
	 * @param password The password of the user.
	 * @return The user object if the username and password are valid, otherwise null.
	 */
	private User findByCredential(String username, String password) {
		User user = userRepository.findByEmailOrPhoneNo(username, username);
		if (user == null) {
			throw new UsernameNotFoundException(ToastMessage.USER_NOT_FOUND);
		}
		boolean check = passwordEncoder.matches(password, user.getPassword());
		if (!check) {
			return null;
		}
		return user;
	}


}
