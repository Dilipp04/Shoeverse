package com.shopverse.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

public final class ProductRequests {

    private ProductRequests() {}

    @Data
    public static class ProductRequest {
        @NotBlank(message = "Product name is required")
        private String name;

        private String description;

        @NotNull @DecimalMin("0.01")
        private BigDecimal price;

        private BigDecimal discountPrice;

        @NotNull @Min(0)
        private Integer stockQuantity;

        @NotBlank
        private String sku;

        private String brand;

        @NotNull
        private Long categoryId;

        private String imageUrl;

        private Boolean active = true;
    }
}
