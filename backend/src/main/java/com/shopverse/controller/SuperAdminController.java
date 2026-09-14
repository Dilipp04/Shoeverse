package com.shopverse.controller;

import com.shopverse.dto.request.AdminUserRequest;
import com.shopverse.dto.response.ApiResponse;
import com.shopverse.dto.response.PageResponse;
import com.shopverse.dto.response.UserResponse;
import com.shopverse.service.UserManagementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/super-admin")
@RequiredArgsConstructor
@Tag(name = "Super Admin")
public class SuperAdminController {

    private final UserManagementService userManagementService;

    @GetMapping("/admins")
    public ApiResponse<PageResponse<UserResponse>> getAdmins(@PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success("Admins fetched successfully", userManagementService.getAdmins(pageable));
    }

    @GetMapping("/users")
    public ApiResponse<PageResponse<UserResponse>> getUsers(@PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success("Users fetched successfully", userManagementService.getAllUsers(pageable));
    }

    @PostMapping("/admins")
    public ApiResponse<UserResponse> createAdmin(@Valid @RequestBody AdminUserRequest request) {
        return ApiResponse.success("Admin created successfully", userManagementService.createAdmin(request));
    }

    @PutMapping("/admins/{id}")
    public ApiResponse<UserResponse> updateAdmin(@PathVariable Long id, @Valid @RequestBody AdminUserRequest request) {
        return ApiResponse.success("Admin updated successfully", userManagementService.updateAdmin(id, request));
    }

    @PatchMapping("/admins/{id}/status")
    public ApiResponse<UserResponse> toggleAdminStatus(@PathVariable Long id, @RequestParam boolean enabled) {
        return ApiResponse.success("Admin status updated", userManagementService.toggleAdminStatus(id, enabled));
    }
}
