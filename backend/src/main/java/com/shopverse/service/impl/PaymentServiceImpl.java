package com.shopverse.service.impl;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.shopverse.dto.request.PaymentVerifyRequest;
import com.shopverse.dto.response.OrderResponse;
import com.shopverse.dto.response.PaymentOrderResponse;
import com.shopverse.entity.*;
import com.shopverse.entity.enums.OrderStatus;
import com.shopverse.entity.enums.PaymentStatus;
import com.shopverse.exception.BadRequestException;
import com.shopverse.exception.PaymentException;
import com.shopverse.exception.ResourceNotFoundException;
import com.shopverse.mapper.OrderMapper;
import com.shopverse.repository.*;
import com.shopverse.security.SecurityUtils;
import com.shopverse.service.CartService;
import com.shopverse.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final RazorpayClient razorpayClient;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final CartService cartService;

    @Value("${razorpay.key-id}")
    private String razorpayKeyId;

    @Value("${razorpay.key-secret}")
    private String razorpayKeySecret;

    @Override
    @Transactional
    public PaymentOrderResponse createRazorpayOrder(Order order) {
        try {
            JSONObject options = new JSONObject();
            options.put("amount", toPaise(order.getTotalAmount()));
            options.put("currency", "INR");
            options.put("receipt", order.getOrderNumber());
            options.put("payment_capture", 1);

            com.razorpay.Order razorpayOrder = razorpayClient.orders.create(options);
            String razorpayOrderId = razorpayOrder.get("id");

            order.setRazorpayOrderId(razorpayOrderId);
            order.setPaymentStatus(PaymentStatus.PENDING);
            orderRepository.save(order);

            paymentRepository.save(Payment.builder()
                    .order(order)
                    .razorpayOrderId(razorpayOrderId)
                    .amount(order.getTotalAmount())
                    .currency("INR")
                    .status(PaymentStatus.PENDING)
                    .build());

            return PaymentOrderResponse.builder()
                    .orderId(order.getId())
                    .orderNumber(order.getOrderNumber())
                    .razorpayOrderId(razorpayOrderId)
                    .razorpayKeyId(razorpayKeyId)
                    .amount(order.getTotalAmount())
                    .currency("INR")
                    .build();
        } catch (RazorpayException ex) {
            order.setPaymentStatus(PaymentStatus.FAILED);
            orderRepository.save(order);
            throw new PaymentException("Failed to create Razorpay order: " + ex.getMessage(), ex);
        }
    }

    @Override
    @Transactional
    public OrderResponse verifyPayment(PaymentVerifyRequest request) {
        Order order = orderRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(SecurityUtils.getCurrentUserId())) {
            throw new BadRequestException("Unauthorized payment verification");
        }

        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            return OrderMapper.toResponse(order);
        }

        if (paymentRepository.existsByRazorpayPaymentIdAndStatus(
                request.getRazorpayPaymentId(), PaymentStatus.PAID)) {
            throw new BadRequestException("Payment already verified");
        }

        try {
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", request.getRazorpayOrderId());
            attributes.put("razorpay_payment_id", request.getRazorpayPaymentId());
            attributes.put("razorpay_signature", request.getRazorpaySignature());

            boolean valid = Utils.verifyPaymentSignature(attributes, razorpayKeySecret);
            if (!valid) {
                markPaymentFailed(order, request);
                throw new PaymentException("Invalid payment signature");
            }
        } catch (Exception ex) {
            markPaymentFailed(order, request);
            throw new PaymentException("Payment verification failed: " + ex.getMessage(), ex);
        }

        return confirmPayment(order, request);
    }

    @Override
    @Transactional
    public void handleWebhook(String payload, String signature) {
        try {
            Utils.verifyWebhookSignature(payload, signature, razorpayKeySecret);
        } catch (RazorpayException ex) {
            throw new PaymentException("Invalid webhook signature");
        }

        JSONObject event = new JSONObject(payload);
        String eventType = event.getString("event");

        if ("payment.captured".equals(eventType)) {
            JSONObject paymentEntity = event.getJSONObject("payload")
                    .getJSONObject("payment").getJSONObject("entity");
            String razorpayOrderId = paymentEntity.getString("order_id");
            String razorpayPaymentId = paymentEntity.getString("id");

            orderRepository.findByRazorpayOrderId(razorpayOrderId).ifPresent(order -> {
                if (order.getPaymentStatus() != PaymentStatus.PAID) {
                    PaymentVerifyRequest request = new PaymentVerifyRequest();
                    request.setRazorpayOrderId(razorpayOrderId);
                    request.setRazorpayPaymentId(razorpayPaymentId);
                    request.setRazorpaySignature("webhook-verified");
                    confirmPaymentInternal(order, request);
                }
            });
        } else if ("payment.failed".equals(eventType)) {
            JSONObject paymentEntity = event.getJSONObject("payload")
                    .getJSONObject("payment").getJSONObject("entity");
            String razorpayOrderId = paymentEntity.optString("order_id", null);
            if (razorpayOrderId != null) {
                orderRepository.findByRazorpayOrderId(razorpayOrderId).ifPresent(order -> {
                    order.setPaymentStatus(PaymentStatus.FAILED);
                    orderRepository.save(order);
                });
            }
        }
    }

    private OrderResponse confirmPayment(Order order, PaymentVerifyRequest request) {
        confirmPaymentInternal(order, request);
        return OrderMapper.toResponse(orderRepository.findByIdWithDetails(order.getId())
                .orElse(order));
    }

    private void confirmPaymentInternal(Order order, PaymentVerifyRequest request) {
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            return;
        }

        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            if (product.getStockQuantity() < item.getQuantity()) {
                order.setPaymentStatus(PaymentStatus.FAILED);
                orderRepository.save(order);
                throw new PaymentException("Insufficient stock during payment confirmation");
            }
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);
        }

        order.setPaymentStatus(PaymentStatus.PAID);
        order.setOrderStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId()).ifPresent(payment -> {
            payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
            payment.setRazorpaySignature(request.getRazorpaySignature());
            payment.setStatus(PaymentStatus.PAID);
            paymentRepository.save(payment);
        });

        cartService.clearCartForUser(order.getUser().getId());
    }

    private void markPaymentFailed(Order order, PaymentVerifyRequest request) {
        order.setPaymentStatus(PaymentStatus.FAILED);
        orderRepository.save(order);
        paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId()).ifPresent(payment -> {
            payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
        });
    }

    private int toPaise(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).intValue();
    }
}
