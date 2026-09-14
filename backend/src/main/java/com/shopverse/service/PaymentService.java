package com.shopverse.service;

import com.shopverse.dto.request.PaymentVerifyRequest;
import com.shopverse.dto.response.OrderResponse;
import com.shopverse.dto.response.PaymentOrderResponse;
import com.shopverse.entity.Order;

public interface PaymentService {
    PaymentOrderResponse createRazorpayOrder(Order order);
    OrderResponse verifyPayment(PaymentVerifyRequest request);
    void handleWebhook(String payload, String signature);
}
