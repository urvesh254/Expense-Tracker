package com.ukpatel.expense.tracker.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import static com.ukpatel.expense.tracker.common.constant.CmnConstants.REGEX_EMAIL;

@Data
@Builder
public class ChangePasswordRequestDTO {

    @NotBlank(message = "Email is required and cannot be blank")
    @Pattern(regexp = REGEX_EMAIL, message = "Email should be in valid format")
    private String email;

    @NotBlank(message = "Password is required and cannot be blank")
    @Size(min = 8, max = 20, message = "Password length must be between 8 to 20")
    private String currentPassword;

    @NotBlank(message = "Password is required and cannot be blank")
    @Size(min = 8, max = 20, message = "Password length must be between 8 to 20")
    private String newPassword;
}
