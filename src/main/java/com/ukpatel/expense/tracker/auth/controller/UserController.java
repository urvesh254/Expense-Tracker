package com.ukpatel.expense.tracker.auth.controller;

import com.ukpatel.expense.tracker.auth.dto.UserDTO;
import com.ukpatel.expense.tracker.auth.service.UserMstService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserMstService userMstService;

    @GetMapping
    public ResponseEntity<?> getUserInfo() {
        UserDTO userDTO = userMstService.getUserInfo();
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping
    public ResponseEntity<?> updateUserInfo(
            @Valid @ModelAttribute UserDTO userDTO
    ) {
        userDTO = userMstService.updateUserInfo(userDTO);
        return ResponseEntity.ok(userDTO);
    }

}
