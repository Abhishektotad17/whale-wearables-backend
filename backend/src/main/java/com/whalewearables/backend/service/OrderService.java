package com.whalewearables.backend.service;

import com.whalewearables.backend.dto.OrderRequest;
import com.whalewearables.backend.model.Order;

public interface OrderService {
    Order createOrder(OrderRequest request);
    void updateOrderStatus(String orderId, String status);
    Order getOrder(String orderId);
}
