package com.shopverse.service;

import com.shopverse.dto.request.ProductRequests;
import com.shopverse.dto.response.PageResponse;
import com.shopverse.dto.response.ProductResponse;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ProductService {
    PageResponse<ProductResponse> getProducts(Pageable pageable);
    ProductResponse getById(Long id);
    ProductResponse getBySlug(String slug);
    PageResponse<ProductResponse> search(String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    PageResponse<ProductResponse> getByCategory(Long categoryId, Pageable pageable);
    PageResponse<ProductResponse> getDiscounted(Pageable pageable);
    ProductResponse create(ProductRequests.ProductRequest request);
    ProductResponse update(Long id, ProductRequests.ProductRequest request);
    void delete(Long id);
}
