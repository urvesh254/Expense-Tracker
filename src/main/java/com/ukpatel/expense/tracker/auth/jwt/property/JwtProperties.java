package com.ukpatel.expense.tracker.auth.jwt.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("security.jwt")
public class JwtProperties {

    private String secreteKey;
    private Long loginTokenValidity;
    private Long changePwdTokenValidity;
}
