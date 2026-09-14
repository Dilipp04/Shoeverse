package com.shopverse.service;

import com.shopverse.dto.request.AdminUserRequest;
import com.shopverse.dto.response.PageResponse;
import com.shopverse.dto.response.UserResponse;
import org.springframework.data.domain.Pageable;

public interface UserManagementService {
    PageResponse<UserResponse> getAdmins(Pageable pageable);
    PageResponse<UserResponse> getAllUsers(Pageable pageable);
    UserResponse createAdmin(AdminUserRequest request);
    UserResponse updateAdmin(Long id, AdminUserRequest request);
    UserResponse toggleAdminStatus(Long id, boolean enabled);
}
