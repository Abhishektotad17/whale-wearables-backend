package com.whalewearables.backend.service.impl;

import com.whalewearables.backend.dto.BillingDTO;
import com.whalewearables.backend.dto.OrderRequest;
import com.whalewearables.backend.dto.ShippingDTO;
import com.whalewearables.backend.model.Order;
import com.whalewearables.backend.model.OrderBilling;
import com.whalewearables.backend.model.OrderItem;
import com.whalewearables.backend.model.OrderShipping;
import com.whalewearables.backend.repository.OrderRepository;
import com.whalewearables.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

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

        // --- Map Billing ---
        if (request.getBilling() != null) {
            BillingDTO billingDTO = request.getBilling();
            OrderBilling billing = new OrderBilling();
            billing.setFullName(billingDTO.getFullName());
            billing.setAddressLine1(billingDTO.getAddressLine1());
            billing.setAddressLine2(billingDTO.getAddressLine2());
            billing.setCity(billingDTO.getCity());
            billing.setState(billingDTO.getState());
            billing.setPostalCode(billingDTO.getPostalCode());
            billing.setCountry(billingDTO.getCountry());
            billing.setOrder(order); // link back
            order.setBilling(billing);
        }

        // --- Map Shipping ---
        if (request.getShipping() != null) {
            ShippingDTO shippingDTO = request.getShipping();
            OrderShipping shipping = new OrderShipping();
            shipping.setFullName(shippingDTO.getFullName());
            shipping.setAddressLine1(shippingDTO.getAddressLine1());
            shipping.setAddressLine2(shippingDTO.getAddressLine2());
            shipping.setCity(shippingDTO.getCity());
            shipping.setState(shippingDTO.getState());
            shipping.setPostalCode(shippingDTO.getPostalCode());
            shipping.setCountry(shippingDTO.getCountry());
            shipping.setOrder(order);
            order.setShipping(shipping);
        }

        // --- Map Items ---
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            order.setItems(
                    request.getItems().stream().map(itemDTO -> {
                        OrderItem item = new OrderItem();
                        item.setProductId(itemDTO.getProductId());
                        item.setProductName(itemDTO.getProductName());
                        item.setQuantity(itemDTO.getQuantity());
                        item.setPrice(itemDTO.getPrice());
                        item.setOrder(order);
                        return item;
                    }).collect(Collectors.toList())
            );
        }

        return orderRepository.save(order);
    }

    @Override
    public void updateOrderStatus(String orderId, String status) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    @Override
    public Order getOrder(String orderId) {
        return orderRepository.findByOrderId(orderId).orElse(null);
    }
}
