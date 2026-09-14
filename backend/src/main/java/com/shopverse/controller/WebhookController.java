package com.shopverse.controller;

import com.shopverse.dto.response.ApiResponse;
import com.shopverse.service.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
@Tag(name = "Webhooks")
public class WebhookController {

    private final PaymentService paymentService;

    @PostMapping("/razorpay")
    public ApiResponse<Void> handleRazorpayWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {
        paymentService.handleWebhook(payload, signature);
        return ApiResponse.success("Webhook processed successfully");
    }
}
