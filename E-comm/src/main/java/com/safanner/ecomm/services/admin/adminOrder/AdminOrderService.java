package com.safanner.ecomm.services.admin.adminOrder;

import com.safanner.ecomm.dto.AnalyticsResponse;
import com.safanner.ecomm.dto.OrderDto;

import java.util.List;

public interface AdminOrderService {
    List<OrderDto> getAllPlacedOrders();

    OrderDto changeOrderStatus(Long orderId, String status);

    AnalyticsResponse calculateAnalytics();

    Long getTotalOrdersForMonths(int month, int year);

    Long getTotalEarningsForMonth(int month, int year);
}