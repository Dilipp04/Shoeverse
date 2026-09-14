package com.shopverse.service.impl;

import com.shopverse.dto.response.DashboardResponse;
import com.shopverse.entity.enums.PaymentStatus;
import com.shopverse.entity.enums.Role;
import com.shopverse.repository.OrderRepository;
import com.shopverse.repository.ProductRepository;
import com.shopverse.repository.UserRepository;
import com.shopverse.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        BigDecimal totalSales = orderRepository.sumTotalAmountByPaymentStatus(PaymentStatus.PAID);

        List<Map<String, Object>> revenueByMonth = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            LocalDate monthStart = LocalDate.now().minusMonths(i).withDayOfMonth(1);
            LocalDate monthEnd = monthStart.plusMonths(1);
            Instant from = monthStart.atStartOfDay().toInstant(ZoneOffset.UTC);
            Instant to = monthEnd.atStartOfDay().toInstant(ZoneOffset.UTC);
            BigDecimal revenue = orderRepository.sumTotalAmountByPaymentStatusAndDateRange(
                    PaymentStatus.PAID, from, to);

            Map<String, Object> entry = new HashMap<>();
            entry.put("month", monthStart.getMonth().name().substring(0, 3));
            entry.put("revenue", revenue);
            revenueByMonth.add(entry);
        }

        List<Map<String, Object>> salesOverview = List.of(
                Map.of("label", "Paid", "value", orderRepository.countByPaymentStatus(PaymentStatus.PAID)),
                Map.of("label", "Pending", "value", orderRepository.countByPaymentStatus(PaymentStatus.PENDING)),
                Map.of("label", "Failed", "value", orderRepository.countByPaymentStatus(PaymentStatus.FAILED))
        );

        List<Map<String, Object>> ordersOverview = List.of(
                Map.of("label", "Placed", "value", orderRepository.countByOrderStatus(com.shopverse.entity.enums.OrderStatus.PLACED)),
                Map.of("label", "Confirmed", "value", orderRepository.countByOrderStatus(com.shopverse.entity.enums.OrderStatus.CONFIRMED)),
                Map.of("label", "Shipped", "value", orderRepository.countByOrderStatus(com.shopverse.entity.enums.OrderStatus.SHIPPED)),
                Map.of("label", "Delivered", "value", orderRepository.countByOrderStatus(com.shopverse.entity.enums.OrderStatus.DELIVERED))
        );

        return DashboardResponse.builder()
                .totalSales(totalSales != null ? totalSales : BigDecimal.ZERO)
                .totalOrders(orderRepository.count())
                .totalCustomers(userRepository.countByRole(Role.CUSTOMER))
                .totalProducts(productRepository.countByActiveTrue())
                .salesOverview(salesOverview)
                .ordersOverview(ordersOverview)
                .revenueByMonth(revenueByMonth)
                .topProducts(List.of())
                .build();
    }
}
