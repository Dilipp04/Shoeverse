package com.shopverse.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardResponse {
    private BigDecimal totalSales;
    private long totalOrders;
    private long totalCustomers;
    private long totalProducts;
    private List<Map<String, Object>> salesOverview;
    private List<Map<String, Object>> ordersOverview;
    private List<Map<String, Object>> revenueByMonth;
    private List<Map<String, Object>> topProducts;
}
