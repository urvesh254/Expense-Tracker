package com.ukpatel.expense.tracker.auth.jwt.filter;

import com.ukpatel.expense.tracker.auth.dto.UserDTO;
import com.ukpatel.expense.tracker.auth.entity.UserMst;
import com.ukpatel.expense.tracker.auth.jwt.JwtUtils;
import com.ukpatel.expense.tracker.auth.repo.UserMstRepo;
import com.ukpatel.expense.tracker.common.dto.UserSessionInfo;
import com.ukpatel.expense.tracker.exception.ApplicationException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

import static com.ukpatel.expense.tracker.auth.jwt.constant.JwtConstants.AUTHORIZATION_HEADER;
import static com.ukpatel.expense.tracker.auth.jwt.constant.JwtConstants.AUTHORIZATION_PREFIX;
import static com.ukpatel.expense.tracker.common.constant.CmnConstants.USER_SESSION_INFO;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserMstRepo userMstRepo;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // Creating UserSessionInfo object with necessary fields which can used in further business logic
            UserSessionInfo userSessionInfo = UserSessionInfo.builder()
                    .remoteHostName(request.getRemoteHost())
                    .remoteIpAddr(request.getRemoteAddr())
                    .build();
            request.setAttribute(USER_SESSION_INFO, userSessionInfo);

            String jwtToken = request.getHeader(AUTHORIZATION_HEADER);

            // if token is not present then ignore it
            if (!(jwtToken != null && jwtToken.startsWith(AUTHORIZATION_PREFIX))) {
                filterChain.doFilter(request, response);
                return;
            }

            // Retrieving claims from token
            jwtToken = jwtToken.replaceAll(AUTHORIZATION_PREFIX, "");
            Claims claims = jwtUtils.decodeToken(jwtToken);
            Long userId = Long.valueOf(claims.getSubject());
            UserMst userMst = userMstRepo.findById(userId).orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "User not found"));

            UserDTO userDTO = UserDTO.builder()
                    .userId(userId)
                    .email(userMst.getEmail())
                    .fullName(userMst.getUserDtl().getFullName())
                    .dob(userMst.getUserDtl().getDob())
                    .build();
            userSessionInfo.setUserDTO(userDTO);

            // Setting authentication in SecurityContextHolder
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userMst.getEmail(), null, userMst.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);
        } catch (ApplicationException e) {
            resolver.resolveException(request, response, null, e);
        } catch (Exception e) {
            resolver.resolveException(request, response, null, new ApplicationException(HttpStatus.BAD_REQUEST, e.getMessage()));
        }
    }
}