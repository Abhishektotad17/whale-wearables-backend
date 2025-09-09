package com.whalewearables.backend.service;

import com.whalewearables.backend.dto.OrderRequest;
import com.whalewearables.backend.dto.PaymentDTO;
import com.whalewearables.backend.dto.ShippingDTO;
import com.whalewearables.backend.model.Order;
import com.whalewearables.backend.model.OrderShipping;
import com.whalewearables.backend.model.Payments;

public interface OrderService {
    Order createOrder(OrderRequest request);
    void updateOrderStatus(String orderId, String status);
    Order getOrder(String orderId);
    void markOrderAsPaid(PaymentDTO paymentDTO);
    public Payments savePayment(Order order, PaymentDTO dto);
    public void reduceStockAfterPurchase(String orderId);
}
