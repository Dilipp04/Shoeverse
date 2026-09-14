package com.shopverse.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productSlug;
    private String imageUrl;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer quantity;
    private BigDecimal subtotal;
    private Integer availableStock;
}
