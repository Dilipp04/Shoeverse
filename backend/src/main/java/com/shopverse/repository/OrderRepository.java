package com.shopverse.repository;

import com.shopverse.entity.Order;
import com.shopverse.entity.enums.OrderStatus;
import com.shopverse.entity.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    Optional<Order> findByIdAndUserId(Long id, Long userId);

    Optional<Order> findByRazorpayOrderId(String razorpayOrderId);

    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Order> findByOrderStatus(OrderStatus orderStatus, Pageable pageable);

    long countByPaymentStatus(PaymentStatus paymentStatus);

    long countByOrderStatus(OrderStatus orderStatus);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.paymentStatus = :status")
    BigDecimal sumTotalAmountByPaymentStatus(@Param("status") PaymentStatus status);

    @Query("""
            SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o
            WHERE o.paymentStatus = :status
              AND o.createdAt >= :from
              AND o.createdAt < :to
            """)
    BigDecimal sumTotalAmountByPaymentStatusAndDateRange(
            @Param("status") PaymentStatus status,
            @Param("from") Instant from,
            @Param("to") Instant to
    );

    @Query("""
            SELECT o FROM Order o
            LEFT JOIN FETCH o.items
            LEFT JOIN FETCH o.address
            WHERE o.id = :id
            """)
    Optional<Order> findByIdWithDetails(@Param("id") Long id);
}
