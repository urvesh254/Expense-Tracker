package com.ukpatel.expense.tracker.auth.controller;

import com.ukpatel.expense.tracker.auth.dto.LoginRequestDTO;
import com.ukpatel.expense.tracker.auth.dto.RegisterRequestDTO;
import com.ukpatel.expense.tracker.auth.dto.TokenResponseDTO;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ukpatel.expense.tracker.auth.jwt.constant.JwtTokenType.API_ACCESS_TOKEN;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {

    private final JwtUtils jwtUtils;
    private final UserMstService userMstService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        userMstService.saveUser(registerRequestDTO);
        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        System.out.println(loginRequest);
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        Long userId = userMstService.findUserByEmail(loginRequest.getEmail()).get().getUserId();
        String jwtToken = jwtUtils.issueToken(userId, API_ACCESS_TOKEN);

        TokenResponseDTO tokenResponseDTO = TokenResponseDTO.builder()
                .email(loginRequest.getEmail())
                .userId(userId)
                .token(jwtToken)
                .build();

        return ResponseEntity.ok(tokenResponseDTO);
    }
}