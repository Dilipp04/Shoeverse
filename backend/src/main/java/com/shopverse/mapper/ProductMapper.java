package com.shopverse.mapper;

import com.shopverse.dto.response.ProductResponse;
import com.shopverse.entity.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class ProductMapper {

    private ProductMapper() {}

    public static ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .discountPercentage(calculateDiscountPercentage(product.getPrice(), product.getDiscountPrice()))
                .stockQuantity(product.getStockQuantity())
                .sku(product.getSku())
                .brand(product.getBrand())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .imageUrl(product.getImageUrl())
                .active(product.getActive())
                .rating(4.5)
                .createdAt(product.getCreatedAt())
                .build();
    }

    public static BigDecimal effectivePrice(Product product) {
        return product.getDiscountPrice() != null ? product.getDiscountPrice() : product.getPrice();
    }

    private static Integer calculateDiscountPercentage(BigDecimal price, BigDecimal discountPrice) {
        if (discountPrice == null || price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        BigDecimal discount = price.subtract(discountPrice)
                .multiply(BigDecimal.valueOf(100))
                .divide(price, 0, RoundingMode.HALF_UP);
        return discount.intValue();
    }
}
