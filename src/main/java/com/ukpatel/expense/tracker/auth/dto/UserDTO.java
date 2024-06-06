package com.ukpatel.expense.tracker.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class UserDTO {

    private Long userId;
    private String email;
    private String fullName;
    private Date dob;
}
