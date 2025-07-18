package com.code.camping.controller;

import com.code.camping.service.AdminService;
import com.code.camping.service.UserService;
import com.code.camping.utils.dto.request.LoginAdminRequest;
import com.code.camping.utils.dto.request.LoginUserRequest;
import com.code.camping.utils.dto.request.RegisterAdminRequest;
import com.code.camping.utils.dto.request.RegisterUserRequest;
import com.code.camping.utils.dto.response.LoginAdminResponse;
import com.code.camping.utils.dto.response.LoginUserResponse;
import com.code.camping.utils.dto.response.UserResponse;
import com.code.camping.utils.dto.webResponse.WebResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private final UserService userService;
    private final AdminService adminService;

    @PostMapping("/register/user")
    @Operation(summary = "Register new user", description = "Register a new user account")
    public ResponseEntity<WebResponse<UserResponse>> registerUser(@Valid @RequestBody RegisterUserRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());
        UserResponse response = UserResponse.fromUser(userService.create(request));
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(WebResponse.<UserResponse>builder()
                        .data(response)
                        .message("User registered successfully")
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @PostMapping("/register/admin")
    @Operation(summary = "Register new admin", description = "Register a new admin account")
    public ResponseEntity<WebResponse<UserResponse>> registerAdmin(@Valid @RequestBody RegisterAdminRequest request) {
        log.info("Registering new admin with email: {}", request.getEmail());
        UserResponse response = UserResponse.fromUser(adminService.create(request));
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(WebResponse.<UserResponse>builder()
                        .data(response)
                        .message("Admin registered successfully")
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @PostMapping("/login/user")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<WebResponse<LoginUserResponse>> loginUser(@Valid @RequestBody LoginUserRequest request) {
        log.info("User login attempt for email: {}", request.getEmail());
        LoginUserResponse response = userService.login(request);
        
        return ResponseEntity.ok(WebResponse.<LoginUserResponse>builder()
                .data(response)
                .message("User logged in successfully")
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PostMapping("/login/admin")
    @Operation(summary = "Admin login", description = "Authenticate admin and return JWT token")
    public ResponseEntity<WebResponse<LoginAdminResponse>> loginAdmin(@Valid @RequestBody LoginAdminRequest request) {
        log.info("Admin login attempt for email: {}", request.getEmail());
        LoginAdminResponse response = adminService.login(request);
        
        return ResponseEntity.ok(WebResponse.<LoginAdminResponse>builder()
                .data(response)
                .message("Admin logged in successfully")
                .timestamp(LocalDateTime.now())
                .build());
    }
} 