package com.ukpatel.expense.tracker.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerifyAuthCodeDTO {

    @NotBlank(message = "authCode is required and cannot be blank")
    private String authCode;

    @NotBlank(message = "verificationCode is required and cannot be blank")
    private String verificationCode;
}
