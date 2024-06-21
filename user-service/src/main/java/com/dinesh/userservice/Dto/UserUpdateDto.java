package com.dinesh.userservice.Dto;

import com.dinesh.userservice.customValidator.PhoneNo;
import com.dinesh.userservice.customValidator.ZipCode;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {

    @Size(min = 3, max = 50, message = "Invalid firstName")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Special characters aren't allowed in firstname")
    private String firstName;

    @Size(min = 3, max = 50, message = "Invalid lastName")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Special characters aren't allowed in lastname")
    private String lastName;

    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,10}$",
            message= "Invalid email format")
    private String email;

    @PhoneNo
    private String phoneNo;


}
