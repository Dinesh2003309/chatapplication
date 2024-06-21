package com.dinesh.userservice.controller;

import com.dinesh.userservice.Dto.ResetPasswordDto;
import com.dinesh.userservice.payload.Response;
import com.dinesh.userservice.service.UserProfileService;
import com.dinesh.userservice.Dto.UserUpdateDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserProfileController {

    private final UserProfileService userProfileService;


    /**
     * This method is used to edit the user profile by sending a PATCH request to the "/userService/editProfile" endpoint.
     * It takes in the HTTP request object and a UserUpdateDto object as parameters.
     * The method calls the editProfile method of the userProfileService to perform the actual profile update.
     * The response from the service is then converted into a ResponseEntity object and returned.
     *
     * @param request The HTTP request object containing the user profile information.
     * @param updates The object containing the updated user profile information.
     * @return ResponseEntity<Response> The HTTP response object containing the updated user profile information.
     */
    @PatchMapping("/userService/editProfile")
    public ResponseEntity<Response> editUserProfile(HttpServletRequest request, @Valid @RequestBody UserUpdateDto updates){
        Response response=userProfileService.editProfile(request, updates);
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        return ResponseEntity.status(httpStatus).body(response);
    }

    /**
     * Resets the user's password.
     * 
     * This method takes the user's old password, new password, and confirm password as inputs and validates them. 
     * If the passwords are valid, it calls the `changePassword` method of the `userProfileService` to update the password in the database. 
     * The method returns a `ResponseEntity` object with the updated password status.
     * 
     * @param request The HTTP request object.
     * @param resetPasswordDto The DTO object containing the old password, new password, and confirm password.
     * @return The HTTP response object containing the updated password status.
     */
    @PostMapping(value = "/userService/changePassword")
    public ResponseEntity<Response> resetPassword(HttpServletRequest request, @Valid @RequestBody ResetPasswordDto resetPasswordDto) {
        Response response = userProfileService.changePassword(request, resetPasswordDto);
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        return ResponseEntity.status(httpStatus).body(response);
    }

}
