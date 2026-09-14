package com.shopverse.controller;

import com.shopverse.dto.request.PaymentVerifyRequest;
import com.shopverse.dto.response.ApiResponse;
import com.shopverse.dto.response.OrderResponse;
import com.shopverse.service.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/verify")
    public ApiResponse<OrderResponse> verifyPayment(@Valid @RequestBody PaymentVerifyRequest request) {
        return ApiResponse.success("Payment verified successfully", paymentService.verifyPayment(request));
    }
}
