package com.shopverse.service.impl;

import com.shopverse.dto.request.AdminUserRequest;
import com.shopverse.dto.response.PageResponse;
import com.shopverse.dto.response.UserResponse;
import com.shopverse.entity.User;
import com.shopverse.entity.enums.Role;
import com.shopverse.exception.BadRequestException;
import com.shopverse.exception.ResourceNotFoundException;
import com.shopverse.mapper.UserMapper;
import com.shopverse.repository.UserRepository;
import com.shopverse.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getAdmins(Pageable pageable) {
        return PageResponse.from(userRepository.findByRole(Role.ADMIN, pageable).map(UserMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getAllUsers(Pageable pageable) {
        return PageResponse.from(userRepository.findAll(pageable).map(UserMapper::toResponse));
    }

    @Override
    @Transactional
    public UserResponse createAdmin(AdminUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        User admin = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(Role.ADMIN)
                .enabled(true)
                .build();
        return UserMapper.toResponse(userRepository.save(admin));
    }

    @Override
    @Transactional
    public UserResponse updateAdmin(Long id, AdminUserRequest request) {
        User admin = userRepository.findById(id)
                .filter(u -> u.getRole() == Role.ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        userRepository.findByEmail(request.getEmail()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new BadRequestException("Email already in use");
            }
        });

        admin.setName(request.getName());
        admin.setEmail(request.getEmail());
        admin.setPhone(request.getPhone());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            admin.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        return UserMapper.toResponse(userRepository.save(admin));
    }

    @Override
    @Transactional
    public UserResponse toggleAdminStatus(Long id, boolean enabled) {
        User admin = userRepository.findById(id)
                .filter(u -> u.getRole() == Role.ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        admin.setEnabled(enabled);
        return UserMapper.toResponse(userRepository.save(admin));
    }
}
