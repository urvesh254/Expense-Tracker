package com.ukpatel.expense.tracker.auth.controller;

import com.ukpatel.expense.tracker.auth.dto.*;
import com.ukpatel.expense.tracker.auth.jwt.JwtUtils;
import com.ukpatel.expense.tracker.auth.service.UserMstService;
import com.ukpatel.expense.tracker.exception.ApplicationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

import static com.ukpatel.expense.tracker.auth.constants.UserConstants.KEY_EMAIL;
import static com.ukpatel.expense.tracker.auth.jwt.JwtUtils.getJwtTokenFromHeader;
import static com.ukpatel.expense.tracker.auth.jwt.constant.JwtConstants.AUTHORIZATION_HEADER;
import static com.ukpatel.expense.tracker.auth.jwt.constant.JwtTokenType.API_ACCESS_TOKEN;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {

    private final JwtUtils jwtUtils;
    private final UserMstService userMstService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @ModelAttribute RegisterRequestDTO registerRequestDTO
    ) {
        userMstService.saveUser(registerRequestDTO);
        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO loginRequest
    ) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        Long userId = userMstService.findUserByEmail(loginRequest.getEmail())
                .orElseThrow()
                .getUserId();
        String jwtToken = jwtUtils.issueToken(userId, API_ACCESS_TOKEN);

        TokenResponseDTO tokenResponseDTO = TokenResponseDTO.builder()
                .email(loginRequest.getEmail())
                .userId(userId)
                .token(jwtToken)
                .build();

        return ResponseEntity.ok(tokenResponseDTO);
    }

    @PostMapping("/logout")
    public void logout(
            @RequestHeader(value = AUTHORIZATION_HEADER) String token
    ) {
        String jwtToken = getJwtTokenFromHeader(token);
        if (jwtToken.isEmpty()) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "'Authorization' header is missing from the request");
        }
        userMstService.logout(jwtToken);
    }

    @PostMapping("/change-password")
    public void changePassword(
            @RequestHeader(value = AUTHORIZATION_HEADER) String token,
            @Valid @RequestBody ChangePasswordRequestDTO changePasswordRequestDTO
    ) {
        String jwtToken = getJwtTokenFromHeader(token);
        userMstService.changeUserPassword(changePasswordRequestDTO, jwtToken);
    }

    @PostMapping("/generate-auth-code")
    public ResponseEntity<?> generateAuthCodeFromEmail(
            @RequestBody Map<String, Object> reqBody
    ) {
        String email = Optional.ofNullable(reqBody.get(KEY_EMAIL))
                .orElseThrow(() -> new ApplicationException(HttpStatus.BAD_REQUEST, "email is required and cannot be blank"))
                .toString();

        Map<String, Object> resBody = userMstService.generateAuthCodeFromEmail(email);
        return ResponseEntity.ok(resBody);
    }

    @PostMapping("/verify-auth-code")
    public ResponseEntity<?> verifyAuthCode(
            @Valid @RequestBody VerifyAuthCodeDTO verifyAuthCodeDTO
    ) {
        TokenResponseDTO tokenResponseDTO = userMstService.verifyAuthCode(verifyAuthCodeDTO);
        return ResponseEntity.ok(tokenResponseDTO);
    }

    @PostMapping("forgot-password")
    public ResponseEntity<?> handleForgotPasswordRequest(
            @RequestHeader(value = AUTHORIZATION_HEADER) String token,
            @Valid @RequestBody ForgotPasswordRequestDTO forgotPasswordRequestDTO
    ) {
        String resetToken = getJwtTokenFromHeader(token);
        userMstService.handleForgotPasswordRequest(forgotPasswordRequestDTO, resetToken);
        return ResponseEntity.ok().build();
    }
}
