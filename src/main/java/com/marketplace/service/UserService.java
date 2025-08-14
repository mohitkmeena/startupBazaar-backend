package com.marketplace.service;

import com.marketplace.dto.ApiResponse;
import com.marketplace.dto.UserLoginRequest;
import com.marketplace.dto.UserRegisterRequest;
import com.marketplace.exception.BadRequestException;
import com.marketplace.model.User;
import com.marketplace.repository.UserRepository;
import com.marketplace.security.JwtTokenProvider;
import com.marketplace.security.UserPrincipal;

import org.springframework.beans.factory.annotation.Autowired;
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
    private final JwtTokenProvider tokenProvider;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    /** ================= USER REGISTRATION ================= */
    public Map<String, Object> registerUser(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        User user = createUserEntity(request);
        userRepository.save(user);

        return buildLoginResponse(user, "User registered successfully");
    }

    /** ================= USER LOGIN ================= */
    public Map<String, Object> loginUser(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        return buildLoginResponse(user, "Login successful");
    }

    /** ================= HELPER METHODS ================= */
    private User createUserEntity(UserRegisterRequest request) {
        return new User(
                UUID.randomUUID().toString(),
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getPhone(),
                request.getRole(),
                request.getLocation()
        );
    }

    private Map<String, Object> buildLoginResponse(User user, String message) {
        String token = tokenProvider.generateToken(user.getUserId(), user.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", buildUserInfoMap(user));

        return response;
    }

    private Map<String, Object> buildUserInfoMap(User user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("user_id", user.getUserId());
        userInfo.put("name", user.getName());
        userInfo.put("email", user.getEmail());
        userInfo.put("role", user.getRole());
        userInfo.put("location", user.getLocation());
        return userInfo;
    }

    /** ================= USER FETCHING ================= */
    public User findByUserId(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }

    /** ================= SPRING SECURITY METHODS ================= */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(UserPrincipal::create)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public UserDetails loadUserByUserId(String userId) {
        return userRepository.findByUserId(userId)
                .map(UserPrincipal::create)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
    }
}
