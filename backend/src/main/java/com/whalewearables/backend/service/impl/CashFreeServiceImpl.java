package com.whalewearables.backend.service.impl;

import com.cashfree.pg.ApiException;
import com.cashfree.pg.ApiResponse;
import com.cashfree.pg.Cashfree;
import com.cashfree.pg.model.CreateOrderRequest;
import com.cashfree.pg.model.CustomerDetails;
import com.cashfree.pg.model.OrderEntity;
import com.whalewearables.backend.model.Order;
import com.whalewearables.backend.repository.OrderRepository;
import com.whalewearables.backend.service.CashFreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.cashfree.pg.Cashfree;

import java.util.Optional;

@Service
public class CashFreeServiceImpl implements CashFreeService {

    @Value("${cashfree.client-id}")
    private String clientId;

    @Value("${cashfree.client-secret}")
    private String clientSecret;

    @Autowired
    private OrderRepository orderRepository;

    // Helper method to initialize the Cashfree object
    private Cashfree initCashfree() {
        // For now, passing `null` for environment/date/version args
        return new Cashfree(Cashfree.CFEnvironment.SANDBOX, clientId, clientSecret, null,null, null);
    }

    @Override
    public String generateToken(String orderId) {
        Cashfree cashfree = initCashfree();

        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            throw new RuntimeException("Order not found with ID: " + orderId);
        }

        Order order = orderOptional.get();

        // Create the order request
        CreateOrderRequest request = new CreateOrderRequest();
        request.setOrderId(order.getOrderId());
        request.setOrderAmount(order.getAmount());
        request.setOrderCurrency("INR");

        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setCustomerPhone(order.getPhone());
        customerDetails.setCustomerId("cust_" + order.getPhone());
        request.setCustomerDetails(customerDetails);

        try {
            ApiResponse<OrderEntity> response = cashfree.PGCreateOrder(request, null, null, null);
            return response.getData().getPaymentSessionId();
        } catch (ApiException e) {
            throw new RuntimeException("Failed to generate Cashfree token: " + e.getMessage(), e);
        }
    }

    @Override
    public String updateOrderStatusFromGateway(String orderId) {
        Cashfree cashfree = initCashfree();

        try {
            ApiResponse<OrderEntity> response = cashfree.PGFetchOrder(orderId, null, null, null);
            String gatewayStatus = response.getData().getOrderStatus();

            if (gatewayStatus == null) {
                gatewayStatus = "UNKNOWN";
            }

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

            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                if (!normalizedStatus.equals(order.getStatus())) {
                    order.setStatus(normalizedStatus);
                    orderRepository.save(order);
                }
            }

            return normalizedStatus;

        } catch (ApiException e) {
            throw new RuntimeException("Failed to fetch order status from Cashfree: " + e.getMessage(), e);
        }
    }




}

