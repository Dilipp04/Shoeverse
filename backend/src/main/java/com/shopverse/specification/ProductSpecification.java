package com.shopverse.specification;

import com.shopverse.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public final class ProductSpecification {

    private ProductSpecification() {}

    public static Specification<Product> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("active"));
    }

    public static Specification<Product> hasCategory(Long categoryId) {
        return (root, query, cb) ->
                categoryId == null ? cb.conjunction() : cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Product> keywordSearch(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern),
                    cb.like(cb.lower(root.get("brand")), pattern)
            );
        };
    }

    public static Specification<Product> priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            var effectivePrice = cb.coalesce(
                    root.<BigDecimal>get("discountPrice"),
                    root.<BigDecimal>get("price")
            );
            if (minPrice != null && maxPrice != null) {
                return cb.and(cb.greaterThanOrEqualTo(effectivePrice, minPrice),
                        cb.lessThanOrEqualTo(effectivePrice, maxPrice));
            }
            if (minPrice != null) {
                return cb.greaterThanOrEqualTo(effectivePrice, minPrice);
            }
            if (maxPrice != null) {
                return cb.lessThanOrEqualTo(effectivePrice, maxPrice);
            }
            return cb.conjunction();
        };
    }

    public static Specification<Product> hasDiscount() {
        return (root, query, cb) -> cb.isNotNull(root.get("discountPrice"));
    }
}
