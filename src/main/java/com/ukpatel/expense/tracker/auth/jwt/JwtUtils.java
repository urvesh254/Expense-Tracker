package com.ukpatel.expense.tracker.auth.jwt;

import com.ukpatel.expense.tracker.auth.jwt.constant.JwtTokenType;
import com.ukpatel.expense.tracker.auth.jwt.property.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.ukpatel.expense.tracker.auth.jwt.constant.JwtConstants.AUTHORIZATION_PREFIX;
import static com.ukpatel.expense.tracker.auth.jwt.constant.JwtConstants.TOKEN_TYPE;

@Component
public class JwtUtils {

    @Autowired
    private JwtProperties jwtProperties;

    public static String getJwtTokenFromHeader(String authorizationHeader) {
        if (!(authorizationHeader != null && authorizationHeader.startsWith(AUTHORIZATION_PREFIX))) {
            return "";
        }
        return authorizationHeader.replaceAll(AUTHORIZATION_PREFIX, "");
    }

    public String issueToken(Long userId, JwtTokenType jwtTokenType) {
        if (jwtTokenType == null) {
            throw new IllegalArgumentException("jwtTokenType cannot be null");
        }

        long validity = switch (jwtTokenType) {
            case API_ACCESS_TOKEN -> jwtProperties.getLoginTokenValidity();
            case CHANGE_PWD_ACCESS_TOKEN -> jwtProperties.getChangePwdTokenValidity();
        };

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim(TOKEN_TYPE, jwtTokenType.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + validity))
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSecreteKey())
                .compact();
    }

    public Claims decodeToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecreteKey())
                .parseClaimsJws(token)
                .getBody();
    }
}
