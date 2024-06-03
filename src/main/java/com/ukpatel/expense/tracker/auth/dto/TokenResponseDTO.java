package com.ukpatel.expense.tracker.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenResponseDTO {

    public Long userId;
    public String email;
    public String token;
}
