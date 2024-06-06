package com.ukpatel.expense.tracker.common.dto;

import com.ukpatel.expense.tracker.auth.dto.UserDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSessionInfo {

    private String remoteIpAddr;
    private String remoteHostName;
    private UserDTO userDTO;
}
