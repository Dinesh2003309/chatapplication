package com.dinesh.userservice.Dto;

import com.dinesh.userservice.customValidator.PhoneNo;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ForgotResetPasswordDto {

    @NotBlank(message = "PhoneNumber is required")
    @PhoneNo
    private String phoneNo;

    @NotBlank(message = "New Password is required")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$!\"#\\$%&'\\(\\)\\*\\+,\\-\\./:;<=>\\?@\\[\\\\\\]\\^_\\{\\|\\}~])[A-Za-z\\d$!\"#\\$%&'\\(\\)\\*\\+,\\-\\./:;<=>\\?@\\[\\\\\\]\\^_\\{\\|\\}~]{8,}$",
            message = "Password must contain a digit, lowercase, uppercase & a special character/ Password must be 8 characters long")
    private String newPassword;

    @NotBlank(message = "Confirm Password is required")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$!\"#\\$%&'\\(\\)\\*\\+,\\-\\./:;<=>\\?@\\[\\\\\\]\\^_\\{\\|\\}~])[A-Za-z\\d$!\"#\\$%&'\\(\\)\\*\\+,\\-\\./:;<=>\\?@\\[\\\\\\]\\^_\\{\\|\\}~]{8,}$",
            message = "Password must contain a digit, lowercase, uppercase & a special character/ Password must be 8 characters long")
    private String confirmPassword;
}
