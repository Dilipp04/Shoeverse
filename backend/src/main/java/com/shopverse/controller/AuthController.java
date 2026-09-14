package com.shopverse.controller;

import com.shopverse.dto.request.AuthRequests;
import com.shopverse.dto.response.ApiResponse;
import com.shopverse.dto.response.AuthResponse;
import com.shopverse.dto.response.UserResponse;
import com.shopverse.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new customer")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody AuthRequests.RegisterRequest request) {
        return ApiResponse.success("Registration successful", authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login and receive JWT token")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody AuthRequests.LoginRequest request) {
        return ApiResponse.success("Login successful", authService.login(request));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user")
    public ApiResponse<UserResponse> me() {
        return ApiResponse.success("User fetched successfully", authService.getCurrentUser());
    }
}
