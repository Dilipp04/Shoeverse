package com.shopverse.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private String paymentStatus;
    private String orderStatus;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal shippingCharge;
    private BigDecimal totalAmount;
    private String razorpayOrderId;
    private AddressResponse address;
    private List<OrderItemResponse> items;
    private Instant createdAt;
    private Instant updatedAt;
}
