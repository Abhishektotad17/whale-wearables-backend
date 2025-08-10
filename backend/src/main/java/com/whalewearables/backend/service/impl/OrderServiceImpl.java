package com.whalewearables.backend.service.impl;

import com.whalewearables.backend.dto.OrderRequest;
import com.whalewearables.backend.model.Order;
import com.whalewearables.backend.repository.OrderRepository;
import com.whalewearables.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Override
    public Order createOrder(OrderRequest request) {
        String orderId = "order_" + UUID.randomUUID(); // Cashfree requires custom orderId

        Order order = new Order();
        order.setOrderId(orderId);
        order.setAmount(request.getAmount());
        order.setPhone(request.getPhone());
        order.setStatus("CREATED");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }

    @Override
    public void updateOrderStatus(String orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order);
    }

    @Override
    public Order getOrder(String orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }
}
