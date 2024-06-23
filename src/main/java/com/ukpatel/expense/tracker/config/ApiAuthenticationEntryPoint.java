package com.ukpatel.expense.tracker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ukpatel.expense.tracker.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN, authException.getMessage());
            ObjectMapper objectMapper = new ObjectMapper();
            String errorResponseJsonStr = objectMapper.writeValueAsString(errorResponse);

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(errorResponseJsonStr);
        }
}
