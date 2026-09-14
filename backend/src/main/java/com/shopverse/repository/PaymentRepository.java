package com.shopverse.repository;

import com.shopverse.entity.Payment;
import com.shopverse.entity.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

    Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId);

    List<Payment> findByOrderId(Long orderId);

    boolean existsByRazorpayPaymentIdAndStatus(String razorpayPaymentId, PaymentStatus status);
}
