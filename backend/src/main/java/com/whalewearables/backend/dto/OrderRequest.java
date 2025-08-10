package com.whalewearables.backend.dto;

import java.math.BigDecimal;

public class OrderRequest {

    private BigDecimal amount;
    private String phone;

    public OrderRequest() {
    }

    public OrderRequest(BigDecimal amount, String phone) {
        this.amount = amount;
        this.phone = phone;
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

    @Override
    public String toString() {
        return "OrderRequest{" +
                "amount=" + amount +
                ", phone='" + phone + '\'' +
                '}';
    }
}
