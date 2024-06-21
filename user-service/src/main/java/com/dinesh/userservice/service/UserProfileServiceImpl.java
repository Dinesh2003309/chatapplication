package com.dinesh.userservice.service;
import com.dinesh.userservice.Dto.ResetPasswordDto;
import com.dinesh.userservice.Dto.UserUpdateDto;
import com.dinesh.userservice.model.User;
import com.dinesh.userservice.payload.Response;
import com.dinesh.userservice.repository.UserRepository;
import com.dinesh.userservice.Dto.*;
import com.dinesh.userservice.constants.Declarations;
import com.dinesh.userservice.constants.ToastMessage;
import com.dinesh.userservice.errorhandler.UserNotFoundException;
import com.dinesh.userservice.model.*;
import com.dinesh.userservice.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.*;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    /**
     * This method is used to edit the profile of a user. It updates the user's information such as first name, last name, phone number, country code, country, zip code, and profile photo.
     * It also saves the updated user information in the database and sends a notification to the user.
     *
     * @param request The HTTP request object containing the user ID.
     * @param updates The DTO object containing the updated user information.
     * @return The response object containing the updated user profile, success message, and success status code.
     */
    @Override
    public Response editProfile(HttpServletRequest request, UserUpdateDto updates) {
        try {
            if (updates.getEmail() != null) {
                return Response.builder().message("Email can't be changed").status(HttpStatus.BAD_REQUEST.value()).success(false).build();
            }
            if (updates.getPhoneNo() != null) {
                return Response.builder().message("PhoneNo can't be changed").status(HttpStatus.BAD_REQUEST.value()).success(false).build();
            }
            User user = userRepository.findById((Integer) request.getAttribute(Declarations.USER_ID))
                    .orElseThrow(() -> new UserNotFoundException(ToastMessage.USER_NOT_FOUND));
            if (updates.getFirstName() != null) {
                user.setFirstName(updates.getFirstName());
            }
            if (updates.getLastName() != null) {
                user.setLastName(updates.getLastName());
            }
            user.setUpdatedAt(Instant.now());
            userRepository.save(user);
            String updatedUser = userRepository.findUserByIdProfile((Integer) request.getAttribute(Declarations.USER_ID));
            return Response.builder().data(new JSONObject(updatedUser).toMap()).message("User profile updated Successfully").status(HttpStatus.OK.value())
                    .success(true).build();
        } catch (Exception e) {
            return Response.builder().message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST.value()).success(false).build();
        }
    }

    /**
     * This method is used to change the password of a user.
     * It checks if the old password provided by the user matches the current password stored in the database.
     * If the old password is correct, it then checks if the new password and the confirm password match.
     * If they match, the method updates the user's password in the database.
     *
     * @param request         The HTTP request object containing the user's information.
     * @param resetPasswordDto The data transfer object containing the old password, new password, and confirm password.
     * @return The response object containing the status, success flag, and message indicating whether the password change was successful or not.
     */
    @Override
    public Response changePassword(HttpServletRequest request, ResetPasswordDto resetPasswordDto) {
        try{
            User user = userRepository.findById((Integer) request.getAttribute(Declarations.USER_ID)).orElseThrow(()-> new UserNotFoundException(ToastMessage.USER_NOT_FOUND));
            if (!passwordEncoder.matches(resetPasswordDto.getOldPassword(), user.getPassword())) {
                return Response.builder().status(HttpStatus.BAD_REQUEST.value()).success(false)
                        .message("Old Password is incorrect").build();
            }
            String oldEncoded = resetPasswordDto.getOldPassword();
            String newEncoded = resetPasswordDto.getNewPassword();
            if (!oldEncoded.equals(newEncoded)) {
                if (resetPasswordDto.getNewPassword().equals(resetPasswordDto.getConfirmPassword())) {
                    user.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
                    userRepository.save(user);
                    return Response.builder().status(HttpStatus.OK.value()).success(true)
                            .message("Your Password has been Updated").build();
                } else {
                    return Response.builder().status(HttpStatus.BAD_REQUEST.value()).success(false)
                            .message("NewPassword and Confirm Password are not Same").build();
                }
            } else {
                return Response.builder().status(HttpStatus.BAD_REQUEST.value()).success(false)
                        .message("New password shouldn't be old password.").build();
            }
        }catch(Exception e){
            return Response.builder().message(e.getMessage()).status(HttpStatus.BAD_REQUEST.value()).success(false).build();
        }
    }



}