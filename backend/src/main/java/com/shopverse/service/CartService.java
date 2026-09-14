package com.shopverse.service;

import com.shopverse.dto.request.CartRequests;
import com.shopverse.dto.response.CartResponse;

public interface CartService {
    CartResponse getCart();
    CartResponse addItem(CartRequests.AddCartItemRequest request);
    CartResponse updateItem(Long itemId, CartRequests.UpdateCartItemRequest request);
    CartResponse removeItem(Long itemId);
    void clearCart();
    void clearCartForUser(Long userId);
}
