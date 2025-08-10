package com.whalewearables.backend.controller;

import com.whalewearables.backend.dto.OrderRequest;
import com.whalewearables.backend.model.Order;
import com.whalewearables.backend.service.CashFreeService;
import com.whalewearables.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private CashFreeService cashFreeService;


    // Step 1: Create order
    @PostMapping("/orders")
    public Order createOrder(@RequestBody OrderRequest request)
    {
        return orderService.createOrder(request);
    }

    // Step 2: Get Cashfree token for the order
    @GetMapping("/orders/{orderId}/token")
    public Map<String, String> getToken(@PathVariable String orderId) {
        String token = cashFreeService.generateToken(orderId);
        return Map.of("cftoken", token);
    }

    // Step 3: Always confirm status with Cashfree & update DB if changed
    @GetMapping("/orders/{orderId}/status")
    public Map<String, String> confirmAndUpdateOrder(@PathVariable String orderId) {
        String status = cashFreeService.updateOrderStatusFromGateway(orderId);
        return Map.of("status", status);
    }
    @GetMapping("/orders/{orderId}")
    public Order getOrder(@PathVariable String orderId) {
        return orderService.getOrder(orderId);
    }

//    @PutMapping("/orders/{orderId}/status")
//    public Map<String, String> updateOrderStatus(@PathVariable String orderId, @RequestParam String status) {
//        orderService.updateOrderStatus(orderId, status);
//        return Map.of("message", "Order status updated successfully");
//    }
}
