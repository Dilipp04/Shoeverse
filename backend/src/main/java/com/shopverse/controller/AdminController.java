package com.shopverse.controller;

import com.shopverse.dto.response.ApiResponse;
import com.shopverse.dto.response.DashboardResponse;
import com.shopverse.dto.response.OrderResponse;
import com.shopverse.dto.response.PageResponse;
import com.shopverse.entity.enums.OrderStatus;
import com.shopverse.service.AdminService;
import com.shopverse.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin")
public class AdminController {

    private final AdminService adminService;
    private final OrderService orderService;

    @GetMapping("/dashboard")
    public ApiResponse<DashboardResponse> getDashboard() {
        return ApiResponse.success("Dashboard fetched successfully", adminService.getDashboard());
    }

    @GetMapping("/orders")
    public ApiResponse<PageResponse<OrderResponse>> getAllOrders(@PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success("Orders fetched successfully", orderService.getAllOrders(pageable));
    }

    @PutMapping("/orders/{id}/status")
    public ApiResponse<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        return ApiResponse.success("Order status updated", orderService.updateOrderStatus(id, status));
    }
}
