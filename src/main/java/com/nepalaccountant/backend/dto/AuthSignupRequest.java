package com.nepalaccountant.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AuthSignupRequest(
		@NotBlank(message = "email is required")
		@Email(message = "email must be valid")
		String email,
		@NotBlank(message = "password is required")
		@Size(min = 8, message = "password must be at least 8 characters")
		String password,
		@NotBlank(message = "firstName is required")
		String firstName,
		String lastName,
		@Pattern(
				regexp = "^$|^(\\+?[0-9]{7,15})$",
				message = "phone must be empty or a valid international number"
		)
		String phone
) {
}
