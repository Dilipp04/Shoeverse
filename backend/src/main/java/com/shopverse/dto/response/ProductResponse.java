package com.shopverse.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Integer discountPercentage;
    private Integer stockQuantity;
    private String sku;
    private String brand;
    private Long categoryId;
    private String categoryName;
    private String imageUrl;
    private Boolean active;
    private Double rating;
    private Instant createdAt;
}
