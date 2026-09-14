package com.shopverse.controller;

import com.shopverse.dto.request.ProductRequests;
import com.shopverse.dto.response.ApiResponse;
import com.shopverse.dto.response.PageResponse;
import com.shopverse.dto.response.ProductResponse;
import com.shopverse.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "List active products with pagination")
    public ApiResponse<PageResponse<ProductResponse>> getProducts(
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.success("Products fetched successfully", productService.getProducts(pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getById(@PathVariable Long id) {
        return ApiResponse.success("Product fetched successfully", productService.getById(id));
    }

    @GetMapping("/slug/{slug}")
    public ApiResponse<ProductResponse> getBySlug(@PathVariable String slug) {
        return ApiResponse.success("Product fetched successfully", productService.getBySlug(slug));
    }

    @GetMapping("/search")
    public ApiResponse<PageResponse<ProductResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @PageableDefault(size = 12) Pageable pageable) {
        return ApiResponse.success("Search results fetched", productService.search(keyword, categoryId, minPrice, maxPrice, pageable));
    }

    @GetMapping("/category/{categoryId}")
    public ApiResponse<PageResponse<ProductResponse>> getByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(size = 12) Pageable pageable) {
        return ApiResponse.success("Category products fetched", productService.getByCategory(categoryId, pageable));
    }

    @GetMapping("/discounted")
    public ApiResponse<PageResponse<ProductResponse>> getDiscounted(
            @PageableDefault(size = 12) Pageable pageable) {
        return ApiResponse.success("Discounted products fetched", productService.getDiscounted(pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ApiResponse<ProductResponse> create(@Valid @RequestBody ProductRequests.ProductRequest request) {
        return ApiResponse.success("Product created successfully", productService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ApiResponse<ProductResponse> update(@PathVariable Long id, @Valid @RequestBody ProductRequests.ProductRequest request) {
        return ApiResponse.success("Product updated successfully", productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ApiResponse.success("Product deleted successfully");
    }
}
