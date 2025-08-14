package com.marketplace.service;

import com.marketplace.dto.ApiResponse;
import com.marketplace.dto.UserLoginRequest;
import com.marketplace.dto.UserRegisterRequest;
import com.marketplace.model.User;
import com.marketplace.repository.UserRepository;
import com.marketplace.security.JwtTokenProvider;
import com.marketplace.security.UserPrincipal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

   
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider tokenProvider;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        //this.tokenProvider = tokenProvider;
    }

    public ApiResponse registerUser(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
      System.out.println("Registering user: " + request.getEmail());
      System.out.println("User role: " + request.getRole());
        String userId = UUID.randomUUID().toString();
        User user = new User(
            userId,
            request.getName(),
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            request.getPhone(),
            request.getRole(),
            request.getLocation()
        );

        userRepository.save(user);

        String token = tokenProvider.generateToken(userId, request.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("user_id", userId);
        userInfo.put("name", request.getName());
        userInfo.put("email", request.getEmail());
        userInfo.put("role", request.getRole());
        userInfo.put("location", request.getLocation());
        response.put("user", userInfo);

        return ApiResponse.success("User registered successfully", response);
    }

    public ApiResponse loginUser(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = tokenProvider.generateToken(user.getUserId(), user.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("user_id", user.getUserId());
        userInfo.put("name", user.getName());
        userInfo.put("email", user.getEmail());
        userInfo.put("role", user.getRole());
        userInfo.put("location", user.getLocation());
        response.put("user", userInfo);

        return ApiResponse.success("Login successful", response);
    }

    public User findByUserId(String userId) {
        return userRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return UserPrincipal.create(user);
    }

    public UserDetails loadUserByUserId(String userId) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        return UserPrincipal.create(user);
    }
}