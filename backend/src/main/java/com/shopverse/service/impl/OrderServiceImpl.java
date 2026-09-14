package com.shopverse.service.impl;

import com.shopverse.dto.request.OrderRequest;
import com.shopverse.dto.response.OrderResponse;
import com.shopverse.dto.response.PageResponse;
import com.shopverse.dto.response.PaymentOrderResponse;
import com.shopverse.entity.*;
import com.shopverse.entity.enums.OrderStatus;
import com.shopverse.entity.enums.PaymentStatus;
import com.shopverse.exception.BadRequestException;
import com.shopverse.exception.InsufficientStockException;
import com.shopverse.exception.ResourceNotFoundException;
import com.shopverse.mapper.OrderMapper;
import com.shopverse.mapper.ProductMapper;
import com.shopverse.repository.*;
import com.shopverse.security.SecurityUtils;
import com.shopverse.service.OrderService;
import com.shopverse.service.PaymentService;
import com.shopverse.util.OrderNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final BigDecimal FREE_SHIPPING_THRESHOLD = BigDecimal.valueOf(999);
    private static final BigDecimal SHIPPING_CHARGE = BigDecimal.valueOf(49);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;

    @Override
    @Transactional
    public PaymentOrderResponse createOrder(OrderRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Address address = addressRepository.findByIdAndUserId(request.getAddressId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new BadRequestException("Cart is empty"));

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        validateStock(cartItems);

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal originalTotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            BigDecimal price = ProductMapper.effectivePrice(product);
            BigDecimal itemSubtotal = price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            subtotal = subtotal.add(itemSubtotal);
            originalTotal = originalTotal.add(
                    product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            orderItems.add(OrderItem.builder()
                    .product(product)
                    .productName(product.getName())
                    .price(price)
                    .quantity(cartItem.getQuantity())
                    .subtotal(itemSubtotal)
                    .build());
        }

        BigDecimal discount = originalTotal.subtract(subtotal).max(BigDecimal.ZERO);
        BigDecimal shipping = subtotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0 ? BigDecimal.ZERO : SHIPPING_CHARGE;
        BigDecimal total = subtotal.add(shipping);

        Order order = Order.builder()
                .orderNumber(OrderNumberGenerator.generate())
                .user(user)
                .address(address)
                .subtotal(subtotal)
                .discount(discount)
                .shippingCharge(shipping)
                .totalAmount(total)
                .paymentStatus(PaymentStatus.CREATED)
                .orderStatus(OrderStatus.PLACED)
                .build();

        order = orderRepository.save(order);

        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }
        orderItemRepository.saveAll(orderItems);
        order.setItems(orderItems);

        return paymentService.createRazorpayOrder(order);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getUserOrders(Pageable pageable) {
        return PageResponse.from(orderRepository
                .findByUserIdOrderByCreatedAtDesc(SecurityUtils.getCurrentUserId(), pageable)
                .map(OrderMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = findUserOrder(id);
        return OrderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderTracking(Long id) {
        return getOrderById(id);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long id) {
        Order order = findUserOrder(id);

        if (order.getOrderStatus() == OrderStatus.SHIPPED || order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new BadRequestException("Order cannot be cancelled at this stage");
        }

        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            order.setPaymentStatus(PaymentStatus.REFUNDED);
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        return OrderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getAllOrders(Pageable pageable) {
        return PageResponse.from(orderRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(OrderMapper::toResponse));
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setOrderStatus(status);
        return OrderMapper.toResponse(orderRepository.save(order));
    }

    private Order findUserOrder(Long id) {
        return orderRepository.findByIdAndUserId(id, SecurityUtils.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    private void validateStock(List<CartItem> cartItems) {
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            if (!product.getActive()) {
                throw new BadRequestException("Product " + product.getName() + " is no longer available");
            }
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for " + product.getName());
            }
        }
    }
}
