package com.whalewearables.backend.controller;

import com.cashfree.pg.model.OrderEntity;
import com.whalewearables.backend.dto.OrderRequest;
import com.whalewearables.backend.dto.OrderResponse;
import com.whalewearables.backend.dto.PaymentDTO;
import com.whalewearables.backend.dto.ShippingDTO;
import com.whalewearables.backend.model.*;
import com.whalewearables.backend.service.CashFreeService;
import com.whalewearables.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

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

    @GetMapping("/orders/{orderId}")
    public Order getOrder(@PathVariable String orderId) {
        return orderService.getOrder(orderId);
    }

    @GetMapping("/orders/{orderId}/verify")
    public  ResponseEntity<?> verifyOrder(@PathVariable String orderId) {
        try {
            // 🔹 Fetch order details once via service
            OrderEntity cfOrder = cashFreeService.fetchOrderDetails(orderId);

            String gatewayStatus = cfOrder.getOrderStatus();
            String normalizedStatus;
            switch (gatewayStatus.toUpperCase()) {
                case "PAID":
                case "SUCCESS":
                case "COMPLETED":
                    normalizedStatus = "PAID";
                    break;
                case "FAILED":
                case "CANCELLED":
                    normalizedStatus = "FAILED";
                    break;
                default:
                    normalizedStatus = gatewayStatus.toUpperCase();
                    break;
            }

            // 🔹 Load local order
            Order order = orderService.getOrder(orderId);
            if (order == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("orderId", orderId, "status", "NOT_FOUND"));
            }

            // 🔹 4. If PAID → fetch actual payments from Cashfree
            if ("PAID".equalsIgnoreCase(normalizedStatus)) {
                var payments = cashFreeService.fetchPayments(orderId);

                // Take the first payment (or loop if you want multiple)
                var cfPayment = payments.get(0);

                // Inside verifyOrder, after fetching cfPayment
                String methodString = "UNKNOWN";
                var cfMethod = cfPayment.getPaymentMethod();

                if (cfMethod != null) {
                    String repr = cfMethod.toString(); // Likely JSON-formatted
                    System.out.println("cfMethod JSON = " + repr);

                    // Attempt simple string search
                    if (repr.toLowerCase().contains("upi")) {
                        methodString = "UPI";
                    } else if (repr.toLowerCase().contains("card")) {
                        methodString = "CARD";
                    } else if (repr.toLowerCase().contains("netbanking")) {
                        methodString = "NETBANKING";
                    } else if (repr.toLowerCase().contains("wallet")) {
                        methodString = "WALLET";
                    }
                }

                PaymentMethod pmEnum;
                try {
                    pmEnum = PaymentMethod.valueOf(methodString);
                } catch (IllegalArgumentException ex) {
                    pmEnum = PaymentMethod.UNKNOWN;
                }

                System.out.println("Mapped payment method = " + pmEnum);


                PaymentDTO paymentDTO = new PaymentDTO(
                        orderId,
                        order.getUser().getId(),
                        cfPayment.getPaymentAmount(),     // ✅ use real paid amount
                        cfPayment.getPaymentCurrency(),   // ✅ use real currency
                        PaymentStatus.valueOf(cfPayment.getPaymentStatus().toString().toUpperCase()), // ✅ enum
                        pmEnum,// ✅ enum
                        cfPayment.getCfPaymentId()        // ✅ real Cashfree paymentId

                );

                // Save payment + mark order paid
                orderService.markOrderAsPaid(paymentDTO);
            } else {
                // Just update status if not paid
                orderService.updateOrderStatus(orderId, normalizedStatus);
            }

            // 🔹 Build response
            OrderResponse responseDTO = new OrderResponse(order);
            responseDTO.setStatus(normalizedStatus);

            return ResponseEntity.ok(responseDTO);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "orderId", orderId,
                    "status", "FAILED",
                    "error", e.getMessage()
            ));
        }
    }

}
