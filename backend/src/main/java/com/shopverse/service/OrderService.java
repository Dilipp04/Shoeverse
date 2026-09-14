package com.shopverse.service;

import com.shopverse.dto.request.OrderRequest;
import com.shopverse.dto.response.OrderResponse;
import com.shopverse.dto.response.PageResponse;
import com.shopverse.dto.response.PaymentOrderResponse;
import com.shopverse.entity.enums.OrderStatus;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    PaymentOrderResponse createOrder(OrderRequest request);
    PageResponse<OrderResponse> getUserOrders(Pageable pageable);
    OrderResponse getOrderById(Long id);
    OrderResponse getOrderTracking(Long id);
    OrderResponse cancelOrder(Long id);
    PageResponse<OrderResponse> getAllOrders(Pageable pageable);
    OrderResponse updateOrderStatus(Long id, OrderStatus status);
}
