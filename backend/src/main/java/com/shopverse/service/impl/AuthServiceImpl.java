package com.shopverse.service.impl;

import com.shopverse.dto.request.AuthRequests;
import com.shopverse.dto.response.AuthResponse;
import com.shopverse.dto.response.UserResponse;
import com.shopverse.entity.User;
import com.shopverse.entity.enums.Role;
import com.shopverse.exception.BadRequestException;
import com.shopverse.exception.UnauthorizedException;
import com.shopverse.mapper.UserMapper;
import com.shopverse.repository.UserRepository;
import com.shopverse.security.CustomUserDetails;
import com.shopverse.security.SecurityUtils;
import com.shopverse.service.AuthService;
import com.shopverse.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(AuthRequests.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();

        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .user(UserMapper.toResponse(user))
                .build();
    }

    @Override
    public AuthResponse login(AuthRequests.LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (Exception ex) {
            throw new UnauthorizedException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!user.getEnabled()) {
            throw new UnauthorizedException("Account is disabled");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return AuthResponse.builder()
                .token(token)
                .user(UserMapper.toResponse(user))
                .build();
    }

    @Override
    public UserResponse getCurrentUser() {
        CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        User user = userRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        return UserMapper.toResponse(user);
    }
}
