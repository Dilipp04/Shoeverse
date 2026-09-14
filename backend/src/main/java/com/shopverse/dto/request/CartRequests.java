package com.shopverse.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

public final class CartRequests {

    private CartRequests() {}

    @Data
    public static class AddCartItemRequest {
        @NotNull
        private Long productId;

        @NotNull @Min(1)
        private Integer quantity;
    }

    @Data
    public static class UpdateCartItemRequest {
        @NotNull @Min(1)
        private Integer quantity;
    }
}
