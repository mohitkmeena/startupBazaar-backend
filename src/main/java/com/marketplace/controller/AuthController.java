package com.marketplace.controller;

import com.marketplace.dto.ApiResponse;
import com.marketplace.dto.UserLoginRequest;
import com.marketplace.dto.UserRegisterRequest;
import com.marketplace.model.User;
import com.marketplace.security.UserPrincipal;
import com.marketplace.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        System.out.println("Registering user: " + request.getEmail());
        System.out.println("User role: " + request.getRole());
        try {
            ApiResponse response = userService.registerUser(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.success(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody UserLoginRequest request) {
        try {
            ApiResponse response = userService.loginUser(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(ApiResponse.success(e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getCurrentUser(@AuthenticationPrincipal UserPrincipal currentUser) {
        User user = userService.findByUserId(currentUser.getUserId());
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("user_id", user.getUserId());
        userInfo.put("name", user.getName());
        userInfo.put("email", user.getEmail());
        userInfo.put("role", user.getRole());
        userInfo.put("location", user.getLocation());

        return ResponseEntity.ok(ApiResponse.data(userInfo));
    }
}