package com.ukpatel.expense.tracker.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@Builder
public class UserDTO {

    private Long userId;
    private String email;
    private String fullName;
    private Date dob;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private MultipartFile profileImg;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String profileImgUrl;
}
