package com.shopverse.controller;

import com.shopverse.dto.request.CartRequests;
import com.shopverse.dto.response.ApiResponse;
import com.shopverse.dto.response.CartResponse;
import com.shopverse.service.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ApiResponse<CartResponse> getCart() {
        return ApiResponse.success("Cart fetched successfully", cartService.getCart());
    }

    @PostMapping("/items")
    public ApiResponse<CartResponse> addItem(@Valid @RequestBody CartRequests.AddCartItemRequest request) {
        return ApiResponse.success("Item added to cart", cartService.addItem(request));
    }

    @PutMapping("/items/{itemId}")
    public ApiResponse<CartResponse> updateItem(@PathVariable Long itemId, @Valid @RequestBody CartRequests.UpdateCartItemRequest request) {
        return ApiResponse.success("Cart item updated", cartService.updateItem(itemId, request));
    }

    @DeleteMapping("/items/{itemId}")
    public ApiResponse<CartResponse> removeItem(@PathVariable Long itemId) {
        return ApiResponse.success("Item removed from cart", cartService.removeItem(itemId));
    }

    @DeleteMapping("/clear")
    public ApiResponse<Void> clearCart() {
        cartService.clearCart();
        return ApiResponse.success("Cart cleared successfully");
    }
}
