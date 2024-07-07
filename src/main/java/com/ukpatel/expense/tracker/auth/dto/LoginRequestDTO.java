package com.ukpatel.expense.tracker.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class LoginRequestDTO {

    @NotBlank(message = "Email is required and cannot be blank")
    @Email(message = "Email should be in valid format")
    private String email;

    @NotBlank(message = "Password is required and cannot be blank")
    @Size(min = 8, max = 20, message = "Password length must be between 8 to 20")
    private String password;
}
