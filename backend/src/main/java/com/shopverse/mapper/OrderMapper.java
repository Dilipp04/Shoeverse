package com.shopverse.mapper;

import com.shopverse.dto.response.OrderItemResponse;
import com.shopverse.dto.response.OrderResponse;
import com.shopverse.entity.Order;
import com.shopverse.entity.OrderItem;

import java.util.List;

public final class OrderMapper {

    private OrderMapper() {}

    public static OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .paymentStatus(order.getPaymentStatus().name())
                .orderStatus(order.getOrderStatus().name())
                .subtotal(order.getSubtotal())
                .discount(order.getDiscount())
                .shippingCharge(order.getShippingCharge())
                .totalAmount(order.getTotalAmount())
                .razorpayOrderId(order.getRazorpayOrderId())
                .address(AddressMapper.toResponse(order.getAddress()))
                .items(mapItems(order.getItems()))
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private static List<OrderItemResponse> mapItems(List<OrderItem> items) {
        if (items == null) {
            return List.of();
        }
        return items.stream().map(item -> OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProductName())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal())
                .build()).toList();
    }
}
