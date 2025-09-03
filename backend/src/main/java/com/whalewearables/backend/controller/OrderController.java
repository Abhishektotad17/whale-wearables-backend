package com.whalewearables.backend.controller;

import com.whalewearables.backend.dto.OrderRequest;
import com.whalewearables.backend.dto.OrderResponse;
import com.whalewearables.backend.model.Order;
import com.whalewearables.backend.service.CashFreeService;
import com.whalewearables.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public OrderResponse  createOrder(@RequestBody OrderRequest request)
    {
        Order order = orderService.createOrder(request);
        return new OrderResponse(order);
    }

    // Step 2: Get Cashfree token for the order
    @GetMapping("/orders/{orderId}/token")
    public Map<String, String> getToken(@PathVariable String orderId) {
        System.out.println("Received orderId: " + orderId);
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

    @GetMapping("/orders/{orderId}/verify")
    public  ResponseEntity<?> verifyOrder(@PathVariable String orderId) {
        try {
            String status = cashFreeService.updateOrderStatusFromGateway(orderId);

            Order order = orderService.getOrder(orderId);

            // Update status in DTO
            OrderResponse response = new OrderResponse(order);
            response.setStatus(status != null ? status : "UNKNOWN");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "orderId", orderId,
                    "status", "FAILED",
                    "error", e.getMessage()
            ));
        }
    }

}
