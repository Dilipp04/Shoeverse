package com.shopverse.service;

import com.shopverse.dto.request.AuthRequests;
import com.shopverse.dto.response.AuthResponse;
import com.shopverse.dto.response.UserResponse;

public interface AuthService {
    AuthResponse register(AuthRequests.RegisterRequest request);
    AuthResponse login(AuthRequests.LoginRequest request);
    UserResponse getCurrentUser();
}
