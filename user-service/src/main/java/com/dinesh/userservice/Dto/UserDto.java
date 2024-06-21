package com.dinesh.userservice.Dto;

import com.dinesh.userservice.customValidator.PhoneNo;
import com.dinesh.userservice.customValidator.ZipCode;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @NotBlank(message = "FirstName is required")
    @Size(min = 3, max = 50, message = "Invalid firstName")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Special characters aren't allowed in firstname")
    private String firstName;

    @NotBlank(message = "LastName is required")
    @Size(min = 3, max = 50, message = "Invalid lastName")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Special characters aren't allowed in lastname")
    private String lastName;

    @NotBlank(message = "Email is Required")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,10}$",
            message= "Invalid email format")
    private String email;

    @NotNull(message = "Phone Number is required")
    @PhoneNo
    private String phoneNo;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$!\"#\\$%&'\\(\\)\\*\\+,\\-\\./:;<=>\\?@\\[\\\\\\]\\^_\\{\\|\\}~])[A-Za-z\\d$!\"#\\$%&'\\(\\)\\*\\+,\\-\\./:;<=>\\?@\\[\\\\\\]\\^_\\{\\|\\}~]{8,}$",
            message = "Password must contain a digit, lowercase, uppercase & a special character/ Password must be 8 characters long")
    private String password;


}
