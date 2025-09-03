package com.whalewearables.backend.dto;

import com.whalewearables.backend.model.Order;

import java.math.BigDecimal;

public class OrderResponse {

    private String orderId;
    private BigDecimal amount;
    private String phone;
    private String status;

    public OrderResponse() {}

    public OrderResponse(Order order) {
        this.orderId = order.getOrderId();
        this.amount = order.getAmount();
        this.phone = order.getPhone();
        this.status = order.getStatus();
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
