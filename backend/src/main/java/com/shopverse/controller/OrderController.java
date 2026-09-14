package com.shopverse.controller;

import com.shopverse.dto.request.OrderRequest;
import com.shopverse.dto.response.ApiResponse;
import com.shopverse.dto.response.OrderResponse;
import com.shopverse.dto.response.PageResponse;
import com.shopverse.dto.response.PaymentOrderResponse;
import com.shopverse.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ApiResponse<PaymentOrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        return ApiResponse.success("Order created successfully", orderService.createOrder(request));
    }

    @GetMapping
    public ApiResponse<PageResponse<OrderResponse>> getUserOrders(@PageableDefault(size = 10) Pageable pageable) {
        return ApiResponse.success("Orders fetched successfully", orderService.getUserOrders(pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrder(@PathVariable Long id) {
        return ApiResponse.success("Order fetched successfully", orderService.getOrderById(id));
    }

    @GetMapping("/{id}/tracking")
    public ApiResponse<OrderResponse> trackOrder(@PathVariable Long id) {
        return ApiResponse.success("Order tracking fetched", orderService.getOrderTracking(id));
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<OrderResponse> cancelOrder(@PathVariable Long id) {
        return ApiResponse.success("Order cancelled successfully", orderService.cancelOrder(id));
    }
}
