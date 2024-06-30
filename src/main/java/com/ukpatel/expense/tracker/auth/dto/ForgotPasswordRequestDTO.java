package com.ukpatel.expense.tracker.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ForgotPasswordRequestDTO {

    @NotBlank(message = "newPassword is required and cannot be blank")
    @Size(min = 8, max = 20, message = "newPassword length must be between 8 to 20")
    private String newPassword;

    @NotBlank(message = "confirmNewPassword is required and cannot be blank")
    @Size(min = 8, max = 20, message = "confirmNewPassword length must be between 8 to 20")
    private String confirmNewPassword;

}
