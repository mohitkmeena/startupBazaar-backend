package com.marketplace.controller;

import com.marketplace.dto.ApiResponse;
import com.marketplace.dto.UserLoginRequest;
import com.marketplace.dto.UserRegisterRequest;
import com.marketplace.model.User;
import com.marketplace.security.UserPrincipal;
import com.marketplace.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterRequest request) {
        logger.info("Attempting to register user: {}", request.getEmail());
        try {
            Map<String, Object> response = userService.registerUser(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error during registration", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequest request) {
        logger.info("User login attempt: {}", request.getEmail());
        try {
            Map<String, Object> response = userService.loginUser(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Login failed for {}: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.status(401).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error during login", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserPrincipal currentUser) {
        logger.debug("Fetching current user info for ID: {}", currentUser.getUserId());
        User user = userService.findByUserId(currentUser.getUserId());

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("user_id", user.getUserId());
        userInfo.put("name", user.getName());
        userInfo.put("email", user.getEmail());
        userInfo.put("role", user.getRole());
        userInfo.put("location", user.getLocation());

        return ResponseEntity.ok(userInfo);
    }
}
