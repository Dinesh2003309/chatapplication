package com.dinesh.userservice.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginRequest {

	@NotBlank(message = "PhoneNo or email is required")
	private String username;

	@NotBlank(message = "Password is required")
	private String password;

	public void setUsername(String username) {
		this.username = username.toLowerCase();
	}

}
