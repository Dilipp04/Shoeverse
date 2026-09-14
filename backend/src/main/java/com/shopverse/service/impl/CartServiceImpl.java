package com.shopverse.service.impl;

import com.shopverse.dto.request.CartRequests;
import com.shopverse.dto.response.CartItemResponse;
import com.shopverse.dto.response.CartResponse;
import com.shopverse.entity.Cart;
import com.shopverse.entity.CartItem;
import com.shopverse.entity.Product;
import com.shopverse.entity.User;
import com.shopverse.exception.BadRequestException;
import com.shopverse.exception.InsufficientStockException;
import com.shopverse.exception.ResourceNotFoundException;
import com.shopverse.mapper.ProductMapper;
import com.shopverse.repository.CartItemRepository;
import com.shopverse.repository.CartRepository;
import com.shopverse.repository.ProductRepository;
import com.shopverse.repository.UserRepository;
import com.shopverse.security.SecurityUtils;
import com.shopverse.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private static final BigDecimal FREE_SHIPPING_THRESHOLD = BigDecimal.valueOf(999);
    private static final BigDecimal SHIPPING_CHARGE = BigDecimal.valueOf(49);

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CartResponse getCart() {
        return buildCartResponse(getOrCreateCart(SecurityUtils.getCurrentUserId()));
    }

    @Override
    @Transactional
    public CartResponse addItem(CartRequests.AddCartItemRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        Cart cart = getOrCreateCart(userId);
        Product product = findActiveProduct(request.getProductId());

        validateStock(product, request.getQuantity());

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);

        if (cartItem != null) {
            int newQty = cartItem.getQuantity() + request.getQuantity();
            validateStock(product, newQty);
            cartItem.setQuantity(newQty);
            cartItem.setPrice(ProductMapper.effectivePrice(product));
            cartItemRepository.save(cartItem);
        } else {
            cartItemRepository.save(CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .price(ProductMapper.effectivePrice(product))
                    .build());
        }

        return buildCartResponse(refreshCart(cart.getId()));
    }

    @Override
    @Transactional
    public CartResponse updateItem(Long itemId, CartRequests.UpdateCartItemRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        CartItem cartItem = cartItemRepository.findByIdAndCartUserId(itemId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        validateStock(cartItem.getProduct(), request.getQuantity());
        cartItem.setQuantity(request.getQuantity());
        cartItem.setPrice(ProductMapper.effectivePrice(cartItem.getProduct()));
        cartItemRepository.save(cartItem);

        return buildCartResponse(refreshCart(cartItem.getCart().getId()));
    }

    @Override
    @Transactional
    public CartResponse removeItem(Long itemId) {
        Long userId = SecurityUtils.getCurrentUserId();
        CartItem cartItem = cartItemRepository.findByIdAndCartUserId(itemId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        Long cartId = cartItem.getCart().getId();
        cartItemRepository.delete(cartItem);
        return buildCartResponse(refreshCart(cartId));
    }

    @Override
    @Transactional
    public void clearCart() {
        clearCartForUser(SecurityUtils.getCurrentUserId());
    }

    @Override
    @Transactional
    public void clearCartForUser(Long userId) {
        cartRepository.findByUserId(userId).ifPresent(cart -> cartItemRepository.deleteByCartId(cart.getId()));
    }

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserIdWithItems(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            return cartRepository.save(Cart.builder().user(user).build());
        });
    }

    private Cart refreshCart(Long cartId) {
        return cartRepository.findById(cartId)
                .flatMap(c -> cartRepository.findByUserIdWithItems(c.getUser().getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
    }

    private Product findActiveProduct(Long productId) {
        return productRepository.findById(productId)
                .filter(Product::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    private void validateStock(Product product, int quantity) {
        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be at least 1");
        }
        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException("Insufficient stock for " + product.getName());
        }
    }

    private CartResponse buildCartResponse(Cart cart) {
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        List<CartItemResponse> itemResponses = items.stream().map(this::toCartItemResponse).toList();

        BigDecimal subtotal = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal originalTotal = items.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = originalTotal.subtract(subtotal).max(BigDecimal.ZERO);
        BigDecimal shipping = subtotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0 ? BigDecimal.ZERO : SHIPPING_CHARGE;
        if (subtotal.compareTo(BigDecimal.ZERO) == 0) {
            shipping = BigDecimal.ZERO;
        }
        BigDecimal total = subtotal.add(shipping);

        return CartResponse.builder()
                .id(cart.getId())
                .items(itemResponses)
                .subtotal(subtotal)
                .discount(discount)
                .shippingCharge(shipping)
                .total(total)
                .itemCount(items.stream().mapToInt(CartItem::getQuantity).sum())
                .build();
    }

    private CartItemResponse toCartItemResponse(CartItem item) {
        Product product = item.getProduct();
        BigDecimal effectivePrice = ProductMapper.effectivePrice(product);
        return CartItemResponse.builder()
                .id(item.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productSlug(product.getSlug())
                .imageUrl(product.getImageUrl())
                .price(effectivePrice)
                .originalPrice(product.getPrice())
                .quantity(item.getQuantity())
                .subtotal(effectivePrice.multiply(BigDecimal.valueOf(item.getQuantity())))
                .availableStock(product.getStockQuantity())
                .build();
    }
}
