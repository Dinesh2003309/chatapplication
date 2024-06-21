package com.dinesh.userservice.service;

import com.dinesh.userservice.Dto.ResetPasswordDto;
import com.dinesh.userservice.Dto.UserUpdateDto;
import com.dinesh.userservice.Dto.*;
import com.dinesh.userservice.payload.Response;
import jakarta.servlet.http.HttpServletRequest;

public interface UserProfileService {


    // Update UserProfile
    Response editProfile(HttpServletRequest request, UserUpdateDto update);

    //Change the password
    Response changePassword( HttpServletRequest request, ResetPasswordDto resetPasswordDto);
}
